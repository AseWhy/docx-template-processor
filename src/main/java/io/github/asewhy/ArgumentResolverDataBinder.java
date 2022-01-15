package io.github.asewhy;

import io.github.asewhy.interfaces.iProvider;
import io.github.asewhy.support.TreeResult;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class ArgumentResolverDataBinder<T, P extends iProvider> implements iProvider {
    private static final Pattern macrosNamePattern = Pattern.compile("[aA-zZаА-яЯ]+");

    private final P parent;
    private final Class<T> clazz;
    private final Map<String, Class<?>> classes = new HashMap<>();
    private final Map<String, Class<?>> resultClasses = new HashMap<>();
    private final Map<String, AccessibleObject> binds = new HashMap<>();
    private final Map<String, List<String>> subspaces = new HashMap<>();
    private final String namespace;
    private final String subspace;

    public ArgumentResolverDataBinder(@NotNull Class<T> clazz, P parent, String namespace) {
        this.clazz = clazz;
        this.namespace = namespace;
        this.subspace = namespace != null ? namespace : ProcessorTypeProvider.MAIN_SUBSPACE;
        this.parent = parent;
        this.subspaces.put(subspace, new ArrayList<>());
    }

    /**
     * Автоматически расставляет зависимые поля {@link ArgumentResolverDataBinder#bind}
     *
     * @return себя любимого
     */
    public ArgumentResolverDataBinder<T, P> auto() throws NoSuchFieldException {
        for(var field: this.clazz.getFields()) {
            bind(field.getName(), field.getName());
        }

        return this;
    }

    /**
     * Добавить поле в набор предобработанных данных
     *
     * @param macros макрос для добавления
     * @param bind добавляемый биндинг
     */
    private void addBind(String macros, TreeResult bind) {
        var key = namespace != null ? namespace + "#" + macros : macros;

        this.resultClasses.put(key, bind.getClazz());
        this.binds.put(key, bind.getAccessible());
        this.classes.put(key, this.clazz);
        this.subspaces.get(this.subspace).add(macros);
    }

    /**
     * Биндит доступ к коллекции элементов поля
     *
     * @param macros название поля источника данных (тип должен быть коллекцией)
     * @throws NoSuchFieldException если поле или метод небыли найдены, или поле не является коллекцией
     */
    @Contract("_ -> new")
    public @NotNull ArgumentResolverDataBinder<?, ArgumentResolverDataBinder<T, P>> sub(String macros) throws NoSuchFieldException {
        var bind = findTree(this.clazz, macros);

        if(bind == null || !Collection.class.isAssignableFrom(bind.getClazz())) {
            throw new NoSuchFieldException("Cannot find collection field " + macros + " in class " + this.clazz.getName() + ".");
        }

        return new ArgumentResolverDataBinder<>(requireGeneric(bind.getAccessible(), macros), this, macros);
    }

    /**
     * Биндит макрос на значение поля класс. То-есть при запросе поля `macros` проводником будет возвращено значение поля
     * `reference` таким образом предоставляя удобную прослойку между обработчиком и объектом на который нацелен данный биндер.
     *
     * @param macros макрос для биндинга
     * @param fieldOrMethodName на какое поле ссылается
     * @return себя любимого
     * @throws NoSuchFieldException если поля в текущем обрабатываемом классе нет
     */
    public ArgumentResolverDataBinder<T, P> bind(String macros, String fieldOrMethodName) throws NoSuchFieldException {
        if(!macrosNamePattern.matcher(macros).matches()) {
            throw new RuntimeException("Bad macros name '" + macros + "'. Allowed only aA-zZаА-яЯ pattern match macros names.");
        }

        var bind = findTree(this.clazz, fieldOrMethodName);

        if(bind != null) {
            addBind(macros, bind);
        } else {
            throw new NoSuchFieldException("Cannot find macros " + fieldOrMethodName + " in class " + this.clazz.getName() + ".");
        }

        return this;
    }

    /**
     * Биндит макрос на значение поля класс. То-есть при запросе поля `macros` проводником будет возвращено значение поля
     * `reference` таким образом предоставляя удобную прослойку между обработчиком и объектом на который нацелен данный биндер.
     *
     * Если производится биндинг, на класс, поставляемый коллекцией, то необходимо указать префикс поля, которое
     * поставляет коллекцию объектов этого класса. Допустим поле root класса N имеет тип X, в самом классе X есть поле root
     * поэтому, чтобы получить доступ именно к полю X.root нужно писать root.root
     *
     * @param macros макрос для биндинга
     * @param fieldOrMethodName на какое поле ссылается
     * @return себя любимого
     * @throws NoSuchFieldException если поля в текущем обрабатываемом классе нет
     */
    @Contract("_, _ -> new")
    public @NotNull ArgumentResolverDataBinder<?, ArgumentResolverDataBinder<T, P>> bindSub(String macros, String fieldOrMethodName) throws NoSuchFieldException {
        if(!macrosNamePattern.matcher(macros).matches()) {
            throw new RuntimeException("Bad macros name '" + macros + "'. Allowed only aA-zZаА-яЯ pattern match macros names.");
        }

        var bind = findTree(this.clazz, fieldOrMethodName);

        if(bind != null) {
            addBind(macros, bind);
        } else {
            throw new NoSuchFieldException("Cannot find field " + fieldOrMethodName + " in class " + this.clazz.getName() + ".");
        }

        if(bind.getClazz() == null || !Collection.class.isAssignableFrom(bind.getClazz())) {
            throw new NoSuchFieldException("Cannot find collection field " + fieldOrMethodName + " in class " + this.clazz.getName() + ".");
        }

        return new ArgumentResolverDataBinder<>(requireGeneric(bind.getAccessible(), macros), this, macros);
    }

    /**
     * Гарантирует наличие generic типа у коллекции
     *
     * @param collection поле или метод, с типом коллекции
     * @param macros поле с названием макроса
     * @return generic тип коллекции
     */
    private Class<?> requireGeneric(AccessibleObject collection, String macros) {
        if(collection instanceof Field field) {
            return Objects.requireNonNull(DocxProcessorsUtils.findXGeneric(field), "Cannot find generic of " + macros);
        } else if(collection instanceof Method method) {
            return Objects.requireNonNull(DocxProcessorsUtils.findXGeneric(method), "Cannot find generic of " + macros);
        } else {
            throw new RuntimeException("Unknown collection type.");
        }
    }

    /**
     * Собирает и возвращает родительский билдер
     *
     * @return родительский билдер
     */
    public P build() {
        this.parent.provide(this.classes, this.resultClasses, this.subspaces, this.binds); return parent;
    }

    /**
     * Ищет член класса по дереву, интерфейсы классы и т.д.
     *
     * @param clazz целевой класс для поиска
     * @param name название члена для поиска
     * @return найденный элемент
     */
    private static @Nullable TreeResult findTree(Class<?> clazz, String name) {
        var bind = (AccessibleObject) null;

        while(bind == null && clazz != null) {
            var interfaces = clazz.getInterfaces();

            if(interfaces.length > 0) {
                var iterator = Arrays.stream(interfaces).iterator();

                while (iterator.hasNext() && bind == null) {
                    var tree = findTree(iterator.next(), name);

                    if(tree != null) {
                        bind = tree.getAccessible();
                    }
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

        if(bind != null) {
            return new TreeResult(bind);
        } else {
            return null;
        }
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
        @NotNull Map<String, List<String>> subspaces,
        @NotNull Map<String, AccessibleObject> binds
    ) {
        this.binds.putAll(binds);
        this.classes.putAll(classes);
        this.resultClasses.putAll(resultClasses);

        for(var subspace: subspaces.entrySet()) {
            var key = subspace.getKey();
            var tags = subspace.getValue();

            if(this.subspaces.containsKey(key)) {
                this.subspaces.get(key).addAll(tags);
            } else {
                this.subspaces.put(key, tags);
            }
        }
    }
}
