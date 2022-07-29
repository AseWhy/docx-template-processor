package io.github.asewhy;

import io.github.asewhy.interfaces.iProvider;
import io.github.asewhy.support.DescriptionEntry;
import io.github.asewhy.support.SubspaceEntry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AccessibleObject;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class ProcessorTypeProvider implements iProvider {
    public final static String MAIN_SUBSPACE = "#MAIN";

    private final Map<String, Class<?>> classes = new HashMap<>();
    private final Map<String, Class<?>> resultClasses = new HashMap<>();
    private final Map<String, AccessibleObject> binds = new HashMap<>();
    private final Map<String, List<String>> subspaces = new HashMap<>();
    private final Map<String, Map<String, DescriptionEntry>> descriptions = new HashMap<>();

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
     * @param classes список соответствий тегов и классов
     * @param resultClasses список соответствий полей (или методов) и их типов (или типов их возвращаемых значений)
     * @param subspaces список соответсвия подпространств и набора тегов (для разных подпространств разный набор тегов)
     * @param binds список соответствий полей и биндингов
     * @param descriptions список описаний для каждого отдельного бинда
     */
    @Override
    public void provide(
        @NotNull Map<String, Class<?>> classes,
        @NotNull Map<String, Class<?>> resultClasses,
        @NotNull Map<String, List<String>> subspaces,
        @NotNull Map<String, AccessibleObject> binds,
        @NotNull Map<String, Map<String, DescriptionEntry>> descriptions
    ) {
        this.classes.putAll(classes);
        this.binds.putAll(binds);
        this.resultClasses.putAll(resultClasses);

        //
        // Добавляем описания
        //
        for(var subspace: descriptions.entrySet()) {
            var key = subspace.getKey();
            var description = subspace.getValue();

            if(this.subspaces.containsKey(key)) {
                this.descriptions.get(key).putAll(description);
            } else {
                this.descriptions.put(key, description);
            }
        }

        //
        // Сортируем названия тегов по возрастанию, ещё на этапе добавления
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

    /**
     * Получить карту описаний тегов
     *
     * @return карта описаний тегов
     */
    public Map<String, Object> getDescriptionMap() {
        var result = new HashMap<String, Object>();
        var spaces = subspaces.entrySet().stream().sorted((a, b) -> Objects.equals(a.getKey(), MAIN_SUBSPACE) ? -1 : Objects.equals(b.getKey(), MAIN_SUBSPACE) ? 0 : 1).collect(Collectors.toList());

        for(var current: spaces) {
            var subspace = current.getKey();
            var tags = current.getValue();

            if(Objects.equals(subspace, MAIN_SUBSPACE)) {
                for(var tag: tags) {
                    var computed = getTagDescription(subspace, tag);

                    if(computed != null) {
                        result.put(tag, computed);
                    }
                }
            } else {
                var subMap = new HashMap<String, DescriptionEntry>();

                for(var tag: tags) {
                    var computed = getTagDescription(subspace, tag);

                    if(computed != null) {
                        subMap.put(tag, computed);
                    }
                }

                result.put(subspace, SubspaceEntry.of((DescriptionEntry) result.get(subspace), subMap));
            }
        }

        return result;
    }

    /**
     * Получить описание для
     *
     * @param subspace подпространство тегов
     * @param tag тег
     * @return описание тега в прод пространстве
     */
    public DescriptionEntry getTagDescription(String subspace, String tag) {
        var descSpace = this.descriptions.get(subspace);

        if(descSpace != null) {
            return descSpace.get(tag);
        } else {
            return null;
        }
    }
}
