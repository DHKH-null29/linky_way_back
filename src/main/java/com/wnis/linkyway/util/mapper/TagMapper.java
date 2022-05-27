package com.wnis.linkyway.util.mapper;

import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TagMapper {
    
    TagMapper instance = Mappers.getMapper(TagMapper.class);
    @Mapping(source = "id", target = "tagId")
    @Mapping(target = "tagName", ignore = true)
    @Mapping(target = "shareable", ignore = true)
    @Mapping(target = "views", ignore = true)
    TagResponse tagToAddTagResponse(Tag tag);
    
    
}
