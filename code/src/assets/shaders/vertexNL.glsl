#version 400 core

uniform mat4 P;
uniform mat4 V;
uniform mat4 M; 	

layout (location = 0) in vec4 vP;		//vectorPosition modelspace
layout (location = 1) in vec2 vT;		//vectorTexture 
layout (location = 2) in vec4 vN;		//vectorNormal 

out vec2 vTexture;

void main(void) {
	gl_Position = P * V * M * vP;	
	vTexture = vT;
}