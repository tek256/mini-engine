#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 normal;

out vec2 outTexCoord;

uniform mat4 projectionModelMatrix;

void main(){
	gl_Position = projectionModelMatrix * vec4(position,1.0);
	outTexCoord = texCoord;
}