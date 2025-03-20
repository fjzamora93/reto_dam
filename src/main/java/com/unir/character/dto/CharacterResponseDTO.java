package com.unir.character.dto;
import com.unir.character.enumm.Gender;
import com.unir.character.enumm.Race;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CharacterResponseDTO {
    private Long id;
    private Long updatedAt;
    private String name;
    private String description;
    private Race race;
    private Gender gender;
    private int level;

    private int armor;
    private int age;
    private int gold;
    private int strength;
    private int dexterity;
    private int constitution;
    private int intelligence;
    private int wisdom;
    private int charisma;
    private String imgUrl;

    // De la sesión solo devolvemos la ID
    private Long gameSessionId;

    // En lugar de solo IDs, podrías devolver objetos con más detalles si es necesario (FUERTE ACOPLAMIENTO, ESTÁS LLEVANDO LA ENTITY ENTERA Y NO UN DTO)
    private Long userId;
    private RoleClassDTO roleClass;
    private List<CustomItemDTO> items;
    private List<CharacterSkillDTO> skills;
}
