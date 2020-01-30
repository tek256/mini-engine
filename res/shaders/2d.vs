#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texcoord;
layout (location=2) in vec3 normal;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform int hasSubOffset;
uniform vec2 subSize;
uniform vec2 subOffset;
uniform vec2 texSize;

out vec2 outTexcoord;

void main(){
	gl_Position = projectionMatrix * modelViewMatrix * vec4(position,1.0);
	if(hasSubOffset == 1){
		outTexcoord = ((subOffset / texSize) + (subSize / texSize) * texcoord);
	}else{
		outTexcoord = texcoord;
	}
}