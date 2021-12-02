package io.github.asewhy.support;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AccessibleObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class ArgumentResolverDataBinder <T, P extends ProcessorTypeProvider> {
    private final Class<T> clazz;
    private final Map<String, AccessibleObject> binds;
    private final P parent;

    public ArgumentResolverDataBinder(@NotNull Class<T> clazz, P parent) {
        this.clazz = clazz;
        this.parent = parent;
        this.binds = new HashMap<>();
    }

    /**
     * Автоматически расставляет зависимые поля {@link ArgumentResolverDataBinder#bind}
     *
     * @return себя любимого
     */
    public ArgumentResolverDataBinder<T, P> auto() {
        for(var field: this.clazz.getFields()) {
            try {
                bind(field.getName(), field.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return this;
    }

    /**
     * Биндит макрос на значение поля класс. То-есть при запросе поля `field` проводником будет возвращено значение поля
     * `reference` таким образом предоставляя удобную прослойку между обработчиком и объектом на который нацелен данный биндер.
     *
     * @param field макрос для биндинга
     * @param reference на какое поле ссылается
     * @return себя любимого
     * @throws NoSuchFieldException если поля в текущем обрабатываемом классе нет
     */
    public ArgumentResolverDataBinder<T, P> bind(String field, String reference) throws NoSuchFieldException {
        var bind = findTree(this.clazz, reference);

        if(bind != null) {
            this.binds.put(field, bind);
        } else {
            throw new NoSuchFieldException("Cannot find field " + reference + " in class " + this.clazz.getName() + ".");
        }

        return this;
    }

    /**
     * Собирает и возвращает родительский билдер
     *
     * @return родительский билдер
     */
    public P build() {
        this.parent.provide(this.clazz, this.binds); return parent;
    }

    /**
     * Ищет член класса по дереву, интерфейсы классы и т.д.
     *
     * @param clazz целевой класс для поиска
     * @param name название члена для поиска
     * @return найденный элемент
     */
    private static AccessibleObject findTree(Class<?> clazz, String name) {
        var bind = (AccessibleObject) null;

        while(bind == null && clazz != null) {
            var interfaces = clazz.getInterfaces();

            if(interfaces.length > 0) {
                var iterator = Arrays.stream(interfaces).iterator();

                while (iterator.hasNext() && bind == null) {
                    bind = findTree(iterator.next(), name);
                }
            }

            if(bind == null) {
                var methods = Arrays.asList(clazz.getDeclaredMethods());

                if(methods.size() > 0) {
                    var iterator = methods.iterator();

                    while(bind == null && iterator.hasNext()) {
                        var method = iterator.next();

                        if(method.getName().equals(name)) {
                            bind = method;
                        }
                    }
                }
            }

            if(bind == null) {
                var fields = Arrays.asList(clazz.getDeclaredFields());

                if(fields.size() > 0) {
                    var iterator = fields.iterator();

                    while(bind == null && iterator.hasNext()) {
                        var method = iterator.next();

                        if(method.getName().equals(name)) {
                            bind = method;
                        }
                    }
                }
            }

            if(bind == null) {
                clazz = clazz.getSuperclass();
            }
        }

        return bind;
    }
}
