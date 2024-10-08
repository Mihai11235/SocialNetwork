package com.example.socialnetwork.utils;


import com.example.socialnetwork.domain.User;

import java.util.*;

public class Graph {
    private static Graph entity = null;

    public static Graph getInstance(){
        if(entity == null)
            entity = new Graph();
        return entity;
    }

    private void dfs(Map<Long, List<User>> graph, Set<Long> visited, Long u){
        visited.add(u);
        for(User v : graph.get(u)){
            if(!visited.contains(v.getId())) {
                dfs(graph, visited, v.getId());
            }
        }
    }

    public int NumberOfConnectedComponents(Map<Long, List<User>> graph){
        Set<Long> visited = new HashSet<>();

        int nr_comp = 0;
        for(Long l : graph.keySet()){
            if(!visited.contains(l)){
                dfs(graph, visited, l);
                nr_comp++;
            }
        }
        return nr_comp;
    }

    private void dfs_drum(Map<Long, List<User>> graph, Set<Long> visited, Long u, List<Long> c){
        c.add(u);
        visited.add(u);
        for(User v : graph.get(u)){
            if(!visited.contains(v.getId())) {
                dfs_drum(graph, visited, v.getId(), c);
            }
        }
    }

    public List<Long> mostSociableCommunity(Map<Long, List<User>> graph){
        List<Long> c = new ArrayList<>();
        Set<Long> visited = new HashSet<>();

        for(Long l : graph.keySet()){
            if(!visited.contains(l)){
                List<Long> aux = new ArrayList<>();
                //aux.add(l);
                dfs_drum(graph, visited, l, aux);
                if(aux.size() > c.size()) {
                    c = aux;
                }
            }
        }
        return c;
    }
}
