#version 330

uniform sampler2D tex;
uniform vec3 ambientLight;
uniform vec2 texRepeat;
uniform vec3 color;
uniform int hasTex;
in vec2 outTexcoord;
out vec4 outcolor;

void main(){
	if(hasTex == 1){
		vec2 trueCoord = outTexcoord * texRepeat;
		vec4 tcolor = texture(tex, trueCoord);
		if(tcolor.a == 0)
			discard;
		outcolor = tcolor * vec4(ambientLight,1.0) * vec4(color,1.0);
	}else{
		outcolor = vec4(color,1.0) * vec4(ambientLight,1.0);
	}
}