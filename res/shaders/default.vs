#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texcoord;
layout (location=2) in vec3 normal;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 modelLightViewMatrix;
uniform mat4 orthoProjectionMatrix;

out vec2 outTexcoord;
out vec3 mvVertexNormal;
out vec3 mvVertexPos;
out vec4 mlightviewVertexPos;
out mat4 outModelViewMatrix;

void main(){
	vec4 mvpos = modelViewMatrix * vec4(position,1.0);
	gl_Position = projectionMatrix * mvpos;
	
	outTexcoord = texcoord;
	mvVertexNormal = normalize(modelViewMatrix * vec4(normal, 0.0)).xyz;
	mvVertexPos = mvpos.xyz;
	mlightviewVertexPos = orthoProjectionMatrix * modelLightViewMatrix * vec4(position, 1.0);
	outModelViewMatrix = modelViewMatrix;
}