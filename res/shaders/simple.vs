#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texcoord;
layout (location=2) in vec3 normal;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

out vec2 outTexcoord;
out vec3 outNormal;
out vec3 mvNormal;
out vec3 mvPosition;
out mat4 outModelView;

void main(){
	vec4 mvpos = modelViewMatrix * vec4(position,1.0);
	gl_Position = projectionMatrix * mvpos;
	mvNormal = normalize(modelViewMatrix * vec4(mvNormal, 0.0)).xyz;
	mvPosition = mvpos.xyz;
	outTexcoord = texcoord;
	outModelView = modelViewMatrix;
}