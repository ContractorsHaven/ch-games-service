package org.binary.scripting.chgamesservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("games")
public class Game {

    @Id
    private UUID id;

    private String name;

    private String description;

    private String genre;

    @Column("min_players")
    private Integer minPlayers;

    @Column("max_players")
    private Integer maxPlayers;

    @Column("image_url")
    private String imageUrl;

    private Boolean active;

    @CreatedBy
    @Column("created_by")
    private String createdBy;

    @LastModifiedBy
    @Column("modified_by")
    private String modifiedBy;
}