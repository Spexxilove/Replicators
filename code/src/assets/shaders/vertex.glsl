#version 400 core

uniform mat4 P;
uniform mat4 V;
uniform mat4 M; 	
uniform vec4 CameraPosition;	

layout (location = 0) in vec4 vP;		//vectorPosition modelspace
layout (location = 1) in vec2 vT;		//vectorTexture 
layout (location = 2) in vec4 vN;		//vectorNormal 

out vec2 vTexture;
out vec3 vNormal;
out vec3 LightDirection;
out vec3 HalfVector;		//camera to position vector
out float AttenuationFrag;

void main(void) {
	vec4 LightPosition = vec4(14.0f, 14.0f, 60.0f, 1.0f);
	float Attenuation = 0.05f;
	gl_Position = P * V * M * vP;
	
	vNormal = normalize(transpose(inverse(mat3(M)))*vN.xyz);	//normalize rotated normal
	vec3 vPosition = vec3(M*vP);
	
	LightDirection = LightPosition.xyz - vPosition; 
	float lightDistance = length(LightDirection); 
   	LightDirection = normalize(LightDirection);
   	
   	HalfVector = normalize(CameraPosition.xyz - vPosition);
	
	AttenuationFrag = 1.0 / (1.0+Attenuation*pow(lightDistance,2));
	
	vTexture = vT;
}