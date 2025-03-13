package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ToDoItemService {

    @Autowired
    private ToDoItemRepository toDoItemRepository;

    public List<ToDoItem> findAll(){
        return toDoItemRepository.findAll();
    }

    public ResponseEntity<ToDoItem> getItemById(int id){
        Optional<ToDoItem> todoData = toDoItemRepository.findById(id);
        return todoData.map(toDoItem -> new ResponseEntity<>(toDoItem, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public ToDoItem addToDoItem(ToDoItem toDoItem){
        return toDoItemRepository.save(toDoItem);
    }

    public boolean deleteToDoItem(int id){
        try {
            toDoItemRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ToDoItem updateToDoItem(int id, ToDoItem td){
        Optional<ToDoItem> toDoItemData = toDoItemRepository.findById(id);
        if (toDoItemData.isPresent()){
            ToDoItem toDoItem = toDoItemData.get();
            toDoItem.setDescription(td.getDescription());
            toDoItem.setCreation_ts(td.getCreation_ts());
            toDoItem.setDone(td.isDone());
            toDoItem.setDeadline(td.getDeadline()); // Ensure deadline is updated
            return toDoItemRepository.save(toDoItem);
        }
        return null;
    }

    // **New Method to Update Only the Deadline**
    public ResponseEntity<ToDoItem> updateDeadline(int id, OffsetDateTime newDeadline) {
        Optional<ToDoItem> toDoItemData = toDoItemRepository.findById(id);
        if (toDoItemData.isPresent()) {
            ToDoItem toDoItem = toDoItemData.get();
            toDoItem.setDeadline(newDeadline);
            return new ResponseEntity<>(toDoItemRepository.save(toDoItem), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
