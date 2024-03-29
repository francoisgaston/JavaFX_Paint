package com.example.tpe_final_poo.backend;

import com.example.tpe_final_poo.backend.model.Figure;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class CanvasState {

    private final Deque<Figure> deque = new ArrayDeque<>();
    
    public void addFigure(Figure figure) {
        deque.add(figure);
    }
    public void moveToBack(Figure figure){
        if (deque.remove(figure)) {
            deque.addFirst(figure);
        }
    }
    public void delete(Figure figure){
        deque.remove(figure);
    }
    public void moveToFront(Figure figure){
        if(deque.remove(figure)){
            deque.addLast(figure);
        }
    }
    public Iterable<Figure> figures() {
        return new ArrayList<>(deque);
    }
}
