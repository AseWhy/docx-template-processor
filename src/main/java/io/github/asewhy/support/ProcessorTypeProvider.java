package io.github.asewhy.support;

import java.lang.reflect.AccessibleObject;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnusedReturnValue")
public class ProcessorTypeProvider {
    private final Map<String, Class<?>> classes = new HashMap<>();
    private final Map<String, AccessibleObject> binds = new HashMap<>();

    /**
     * Представляет собой набор типов.
     *
     * @param target целевой объект
     * @param <T> тип объекта
     * @return билдер биндингов для переданного объекта (класса*)
     */
    public <T> ArgumentResolverDataBinder<T, ProcessorTypeProvider> provide(Class<T> target) {
        return new ArgumentResolverDataBinder<>(target, this);
    }

    /**
     * Предоставить данные по типам этому набору типов
     *
     * @param binds данные по типам
     * @return себя
     */
    protected ProcessorTypeProvider provide(Class<?> clazz, Map<String, AccessibleObject> binds) {
        for(var field: binds.entrySet()) {
            classes.put(field.getKey(), clazz);
        }

        this.binds.putAll(binds);

        return this;
    }

    /**
     * Получить поле по его биндингу
     *
     * @param field биндинг поля
     * @return найденное поле
     */
    public AccessibleObject getBind(String field) {
        return this.binds.get(field);
    }

    /**
     * Получить класс на который забинжено поле field
     *
     * @param field поле для поиска
     * @return класса на который произведен бинд
     */
    public Class<?> getClassForBind(String field) {
        return this.classes.get(field);
    }
}
