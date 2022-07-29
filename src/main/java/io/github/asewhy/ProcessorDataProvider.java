package io.github.asewhy;

import io.github.asewhy.interfaces.iDataResolver;
import io.github.asewhy.json.JsonGenerator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "UnusedReturnValue"})
public class ProcessorDataProvider {
    private final HashMap<Class<?>, iDataResolver<?>> resolvers = new HashMap<>();

    /**
     * Поставлять динамические данных
     *
     * @param clazz класс бинд для которого происходит
     * @param resolver поставщик данных для этого класса
     * @param <T> тип данных, к которому должен принадлежать и класс и обработчик
     * @return текущий поставщик данных
     */
    public <T> ProcessorDataProvider provide(Class<T> clazz, iDataResolver<T> resolver) {
        this.resolvers.put(clazz, resolver); return this;
    }

    /**
     * Поставлять статические данных
     *
     * @param clazz класс бинд для которого происходит
     * @param object статически поставляемые данные для добавления
     * @param <T> тип данных, к которому должен принадлежать и класс и обработчик
     * @return текущий поставщик данных
     */
    public <T> ProcessorDataProvider provide(Class<T> clazz, Object object) {
        this.resolvers.put(clazz, () -> object); return this;
    }

    /**
     * Поставлять данные другого поставщика
     *
     * @param provider поставщик, данные которого нужно так-же учитывать при разрешении зависимостей
     * @return текущий поставщик данных
     */
    public ProcessorDataProvider provide(@NotNull ProcessorDataProvider provider) {
        this.resolvers.putAll(provider.resolvers); return this;
    }

    /**
     * Получить набор данных по значению класса
     *
     * @param forClass класс для которого нужно получить набор данных
     * @return набор данных по значению класса
     */
    public <T> T resolve(Class<T> forClass) {
        var resolver = resolvers.get(forClass);

        if(resolver != null) {
            return (T) resolver.get();
        } else {
            return null;
        }
    }

    /**
     * Создает json строку сообщение с данными в текущем наборе данных.
     */
    protected String getDebugDumpData() {
        var gen = JsonGenerator.common().writeStartObject();

        for(var current: resolvers.entrySet()) {
            var target = current.getKey();
            var superclass = target.getSuperclass();
            var interfaces = target.getInterfaces();
            var provider = current.getValue();
            var provided = provider.get();

            gen.writeStartObject(target.getSimpleName());
            gen.writeField("class", target.getCanonicalName());

            if(superclass != null) {
                gen.writeField("extends", superclass.getCanonicalName());
            }

            if(interfaces.length > 0) {
                gen.writeStartArray("interfaces");

                for(var inter: interfaces) {
                    gen.write(inter.getCanonicalName());
                }

                gen.writeEndArray();
            }

            if(provided != null) {
                var providedClass = provided.getClass();
                var fields = providedClass.getDeclaredFields();
                var methods = providedClass.getDeclaredMethods();

                for (var field : fields) {
                    try {
                        if (!Modifier.isStatic(field.getModifiers())) {
                            var accessible = field.canAccess(provided);
                            field.setAccessible(true);
                            gen.writeField(field.getName(), field.get(provided));
                            field.setAccessible(accessible);
                        }
                    } catch (IllegalAccessException e) {
                        gen.writeField(field.getName(), "<cannot resolve value> ");
                    }
                }

                for (var method : methods) {
                    var args = method.getParameterTypes();
                    var name = method.getName() + "(" + Arrays.stream(args).map(Class::getSimpleName).collect(Collectors.joining(", ")) + ")";
                    gen.writeField(name, "{ /* Some Code Here */ }");
                }
            }

            gen.writeEndObject();
        }

        return gen.writeEndObject().toString();
    }
}
