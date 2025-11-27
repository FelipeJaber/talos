package com.felipejaber.talos.data.repository;

import com.felipejaber.talos.data.entities.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserDataRepository extends JpaRepository<UserData, UUID> {
    UserData findByEmail(String email);
}
