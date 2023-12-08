package me.justin.modules.school;


import java.util.*;

public class SchoolRepository {
    private static final Map<String, School> store = new HashMap<>();

    private SchoolRepository(){}

    private static class SchoolRepositoryHelper {
        private static final SchoolRepository SCHOOL_REPOSITORY = new SchoolRepository();
    }
    public static SchoolRepository getInstance(){
        return SchoolRepositoryHelper.SCHOOL_REPOSITORY;
    }

    public School save(School school) {
        store.put(school.getName(), school);
        return school;
    }

    public boolean existByName(String name){
        return store.containsKey(name);
    }

    public Optional<School> findByName(String name){
        return Optional.ofNullable(store.get(name));
    }

    public List<School> findAllWithoutCountZero(){
        return findAll().stream()
                .filter(school -> !school.getCount().equals(0))
                .toList();
    }

    public List<School> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clearStore() {
        store.clear();
    }
}
