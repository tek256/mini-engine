#version 330

in vec2 outTexCoord;
in vec3 mvPos;

uniform sampler2D tex;
uniform vec3 color;
uniform int hasTexture;

out vec4 fragColor;

void main(){
	if(hasTexture == 1){
		fragColor = vec4(color,1) * texture(tex, outTexCoord);
	}else{
		fragColor = vec4(color,1);
	}
	
	if(fragColor.a == 0)
		discard;
}