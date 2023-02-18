package com.example.party.category.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.example.party.category.dto.CategoryRequest;
import com.example.party.global.TimeStamped;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")
@Entity(name = "category")
public class Category extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "name", nullable = false, unique = true)
	private String name;

	//생성자
	public Category(CategoryRequest request) {
		this.name = request.getName();
	}

	//카테고리 수정
	public void update(CategoryRequest request) {
		this.name = request.getName();
	}

	public void deleteCategory() {
	}
}
