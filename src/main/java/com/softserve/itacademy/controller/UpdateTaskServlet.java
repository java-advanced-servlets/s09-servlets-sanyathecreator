package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.Priority;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.repository.TaskRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/edit-task")
public class UpdateTaskServlet extends HttpServlet {

    private TaskRepository taskRepository;

    @Override
    public void init() {
        taskRepository = TaskRepository.getTaskRepository();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                Task task = taskRepository.read(id);
                if (task != null) {
                    request.setAttribute("task", task);
                    request.getRequestDispatcher("/WEB-INF/pages/edit-task.jsp").forward(request, response);
                } else {
                    request.setAttribute("message", "Task with ID '" + id + "' not found in To-Do List!");
                    request.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(request, response);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("message", "Invalid Task ID format!");
                request.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("message", "Task ID is missing!");
            request.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String title = request.getParameter("title");
        String priorityStr = request.getParameter("priority");

        if (idStr != null && !idStr.isEmpty() && title != null && !title.isEmpty()
                && priorityStr != null && !priorityStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                Priority priority = Priority.valueOf(priorityStr);

                Task existingTask = taskRepository.read(id);
                if (existingTask != null) {
                    String originalTitle = existingTask.getTitle();
                    Priority originalPriority = existingTask.getPriority();
                    existingTask.setTitle(title);
                    existingTask.setPriority(priority);
                    boolean updated = taskRepository.update(existingTask);
                    if (updated) {
                        response.sendRedirect("/tasks-list");
                    } else {
                        existingTask.setTitle(originalTitle);
                        existingTask.setPriority(originalPriority);
                        request.setAttribute("error", "Task with ID '" + id + "': title '" + title + "' already exists in To-Do List!");
                        request.setAttribute("task", existingTask);
                        request.setAttribute("newTitle", title);
                        request.setAttribute("newPriority", priority);
                        request.getRequestDispatcher("/WEB-INF/pages/edit-task.jsp").forward(request, response);
                    }
                } else {
                    request.setAttribute("message", "Task with ID '" + id + "' not found in To-Do List!");
                    request.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(request, response);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("message", "Invalid Task ID format!");
                request.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("message", "Required parameters are missing!");
            request.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(request, response);
        }
    }
}
