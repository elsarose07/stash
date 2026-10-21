package com.ems.dao;

import com.ems.exception.AppException;
import java.util.List;

/**
 * Generic Data Access Object Interface.
 * Demonstrates:
 *  - Module 3: Defining an Interface, Accessing implementations through interface references
 *  - Module 3: Exception Handling (throws clause)
 *
 * @param <T> Model entity type
 */
public interface GenericDAO<T> {
    boolean add(T t) throws AppException;
    boolean update(T t) throws AppException;
    boolean delete(int id) throws AppException;
    T getById(int id) throws AppException;
    List<T> getAll() throws AppException;
}
