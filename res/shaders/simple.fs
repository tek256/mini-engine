#version 330

uniform sampler2D tex;
uniform vec3 ambientLight;

in vec2 outTexcoord;

out vec4 outcolor;

void main(){
	vec4 color = texture(tex, outTexcoord);
	if(color.a == 0)
		discard;
	outcolor = color * vec4(ambientLight,1.0);
}