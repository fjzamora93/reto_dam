package com.unir.roleapp.repository;
import com.unir.roleapp.entity.CharacterEntity;
import com.unir.roleapp.entity.Spell;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpellRepository extends JpaRepository<Spell, Long> {
}
