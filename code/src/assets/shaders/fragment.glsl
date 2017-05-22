#version 400 core

uniform sampler2D texture_diffuse;
uniform float Shininess;		//material component

in vec2 vTexture;
in vec3 vNormal;
in vec3 LightDirection;
in vec3 HalfVector;		//camera to position vector
in float AttenuationFrag;

out vec4 FragColor;

void main(void) {
	float AmbientCoefficient = 0.02f;
	float Strength = 200.0f;
	vec4 LightColor = vec4(1.0f,1.0f,0.8f,1.0f);
	
	vec4 textureColor = texture(texture_diffuse, vTexture);	
	vec3 lightColor = vec3(LightColor);	

   	//diffuse
	float diffuseCoefficient = max(0.0, dot(vNormal,LightDirection)); //both normalized
	vec3 diffuse = diffuseCoefficient*textureColor.rgb*lightColor*Strength;
	
	//specular
	float specularCoefficient = 0.0;
	if(diffuseCoefficient > 0.0)
    	specularCoefficient = pow(max(0.0,dot(HalfVector,reflect(-LightDirection,vNormal))),Shininess);
	vec3 specular = specularCoefficient*vec3(1,1,1)*lightColor*Strength;

	//ambient
 	vec3 ambient = AmbientCoefficient*textureColor.rgb*lightColor;
 	
 	//linear color before gamma correction
	vec3 linearColor = ambient + AttenuationFrag*(diffuse+specular);
	
	//final color after gamma corretion
	vec3 gamma = vec3(1.0/2.2);
	
	FragColor = vec4(pow(linearColor, gamma), textureColor.a); 
}