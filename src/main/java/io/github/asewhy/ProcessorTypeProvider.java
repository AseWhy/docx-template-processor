package io.github.asewhy;

import io.github.asewhy.interfaces.iProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AccessibleObject;
import java.util.*;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class ProcessorTypeProvider implements iProvider {
    public final static String MAIN_SUBSPACE = "#MAIN";

    private final Map<String, Class<?>> classes = new HashMap<>();
    private final Map<String, Class<?>> resultClasses = new HashMap<>();
    private final Map<String, AccessibleObject> binds = new HashMap<>();
    private final Map<String, List<String>> subspaces = new HashMap<>();

    /**
     * Представляет собой набор типов.
     *
     * @param target целевой объект
     * @param <T> тип объекта
     * @return конструктор биндингов для переданного объекта (класса*)
     */
    public <T> ArgumentResolverDataBinder<T, ProcessorTypeProvider> provide(Class<T> target) {
        return new ArgumentResolverDataBinder<>(target, this, null);
    }

    /**
     * Перенести данные из внешнего источника
     *
     * @param classes список соответствий макросов и классов
     * @param resultClasses список соответствий полей (или методов) и их типов (или типов их возвращаемых значений)
     * @param subspaces список соответсвия подпространств и набора тегов (для разных подпространств разный набор тегов)
     * @param binds список соответствий полей и биндингов
     */
    @Override
    public void provide(
        Map<String, Class<?>> classes,
        Map<String, Class<?>> resultClasses,
        Map<String, List<String>> subspaces,
        @NotNull Map<String, AccessibleObject> binds
    ) {
        this.classes.putAll(classes);
        this.binds.putAll(binds);
        this.resultClasses.putAll(resultClasses);

        //
        // Сортируем названия тегов по возростанию, ещё на этапе добавления
        //
        for(var subspace: subspaces.entrySet()) {
            var key = subspace.getKey();
            var tags = subspace.getValue();

            if(this.subspaces.containsKey(key)) {
                this.subspaces.get(key).addAll(tags);
            } else {
                this.subspaces.put(key, tags);
            }

            this.subspaces.get(key).sort((a, b) -> b.length() - a.length());
        }
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

    /**
     * Вернет true если класс по этому ключу является коллекцией
     *
     * @param field название поля для доступа
     * @return значение поля для доступа
     */
    public Boolean isCollection(String field) {
        var clazz = this.resultClasses.get(field);

        if(clazz != null) {
            return Collection.class.isAssignableFrom(clazz);
        } else {
            return false;
        }
    }

    /**
     * Получить поддерживаемые подпространства
     *
     * @return список поддерживаемых подпространств
     */
    @Contract(" -> new")
    public List<String> getSupportSubspaces() {
        return new ArrayList<>(subspaces.keySet());
    }

    /**
     * Получить набор тегов для подпространства тегов
     *
     * @param subspace подпространство тегов
     * @return набор тегов этого подпространства
     */
    @Contract("_ -> new")
    public List<String> getSupportTagNames(String subspace) {
        var result = subspaces.get(subspace);

        if(result == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(result);
    }
}
