package com.sclad.scladapp.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractEntityMapper<DB, DTO> {

	@Autowired
	private ModelMapper modelMapper = new ModelMapper();

	private Class<DB> entityClass;
	private Class<DTO> dtoClass;

	protected AbstractEntityMapper(Class<DB> entityClass, Class<DTO> dtoClass) {
		this.entityClass = entityClass;
		this.dtoClass = dtoClass;
	}

	public DTO toDto(DB entity) {
		if (entity == null) {
			return null;
		}
		return modelMapper.map(entity, dtoClass);
	}

	public DB toEntity(DTO dto) {
		if (dto == null) {
			return null;
		}
		return modelMapper.map(dto, entityClass);
	}

}
