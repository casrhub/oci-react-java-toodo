package com.springboot.MyTodoList.controller;
import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.service.ToDoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
public class ToDoItemController {
    @Autowired
    private ToDoItemService toDoItemService;
    //@CrossOrigin
    @GetMapping(value = "/todolist")
    public List<ToDoItem> getAllToDoItems(){
        return toDoItemService.findAll();
    }
    //@CrossOrigin
    @GetMapping(value = "/todolist/{id}")
    public ResponseEntity<ToDoItem> getToDoItemById(@PathVariable int id){
        try{
            ResponseEntity<ToDoItem> responseEntity = toDoItemService.getItemById(id);
            return new ResponseEntity<ToDoItem>(responseEntity.getBody(), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    //@CrossOrigin
    @PostMapping(value = "/todolist")
    public ResponseEntity addToDoItem(@RequestBody ToDoItem todoItem) throws Exception{
        ToDoItem td = toDoItemService.addToDoItem(todoItem);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location",""+td.getID());
        responseHeaders.set("Access-Control-Expose-Headers","location");
        //URI location = URI.create(""+td.getID())

        return ResponseEntity.ok()
                .headers(responseHeaders).build();
    }
    //@CrossOrigin
    @PutMapping(value = "todolist/{id}")
    public ResponseEntity updateToDoItem(@RequestBody ToDoItem toDoItem, @PathVariable int id){
        try{
            ToDoItem toDoItem1 = toDoItemService.updateToDoItem(id, toDoItem);
            System.out.println(toDoItem1.toString());
            return new ResponseEntity<>(toDoItem1,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    //@CrossOrigin
    @DeleteMapping(value = "todolist/{id}")
    public ResponseEntity<Boolean> deleteToDoItem(@PathVariable("id") int id){
        Boolean flag = false;
        try{
            flag = toDoItemService.deleteToDoItem(id);
            return new ResponseEntity<>(flag, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(flag,HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/todolist/{id}/deadline")
public ResponseEntity<ToDoItem> updateDeadline(@PathVariable int id, @RequestBody Map<String, String> requestBody) {
    try {
        // Extract the deadline from JSON request
        String deadlineStr = requestBody.get("deadline");
        if (deadlineStr == null || deadlineStr.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Convert string to OffsetDateTime
        OffsetDateTime newDeadline = OffsetDateTime.parse(deadlineStr);

        // Update the deadline in the database
        return toDoItemService.updateDeadline(id, newDeadline);
    } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}


}