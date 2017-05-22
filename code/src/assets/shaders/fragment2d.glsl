#version 400 core

uniform sampler2D texture_diffuse;

in vec2 vTexture;

out vec4 FragColor;

void main(void) {
	FragColor = texture(texture_diffuse,vTexture);
}