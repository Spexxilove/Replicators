#version 400 core

uniform vec4 PrimitiveColor;

out vec4 FragColor;

void main(void) {
	FragColor = PrimitiveColor;
}