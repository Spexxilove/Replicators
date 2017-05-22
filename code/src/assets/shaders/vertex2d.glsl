#version 400 core

uniform mat4 M;

layout (location = 0) in vec4 vP;		//vectorPosition
layout (location = 1) in vec2 vT;		//vectorTexture 

out vec2 vTexture;

void main(void) {
	gl_Position = M*vP;
	vTexture = vT;
}