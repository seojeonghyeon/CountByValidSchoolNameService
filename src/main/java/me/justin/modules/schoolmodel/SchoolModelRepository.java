package me.justin.modules.schoolmodel;


import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor
public class SchoolModelRepository {
    private static final Map<String, SchoolModel> store = new HashMap<>();

    private static class SchoolRepositoryHelper {
        private static final SchoolModelRepository SCHOOL_REPOSITORY = new SchoolModelRepository();
    }
    public static SchoolModelRepository getInstance(){
        return SchoolRepositoryHelper.SCHOOL_REPOSITORY;
    }

    public SchoolModel save(SchoolModel schoolModel) {
        store.put(schoolModel.getName(), schoolModel);
        return schoolModel;
    }

    public boolean existByName(String name){
        return store.containsKey(name);
    }

    public Optional<SchoolModel> findByName(String name){
        return Optional.ofNullable(store.get(name));
    }

    public List<SchoolModel> findAllWithoutCountZero(){
        return findAll().stream()
                .filter(school -> !school.getCount().equals(0))
                .toList();
    }

    public List<SchoolModel> findAll() {
        return new ArrayList<>(store.values());
    }

    public Queue<SchoolModel> findAllQueueType() {
        return new ArrayDeque<>(store.values());
    }

    public void clearStore() {
        store.clear();
    }
}
