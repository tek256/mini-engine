#version 330

const int MAX_POINT_LIGHTS = 5;

in vec2 outTexcoord;
in vec3 mvVertexNormal;
in vec3 mvVertexPos;
in vec4 mlightviewVertexPos;
in mat4 outModelViewMatrix;

out vec4 fragColor;

struct Attenuation{
	float constant;
	float linear;
	float exponent;
};

struct PointLight{
	vec3 color;
	vec3 position;
	float intensity;
	Attenuation att;
};

struct Material{
	vec3 color;
	sampler2D tex;
	int hasTexture;
	sampler2D normalMap;
	int hasNormalMap;
	float reflect;
};

uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform vec3 camera_position;
uniform sampler2D shadowMap;

vec4 calcLightColor(vec3 light_color, float light_intensity, vec3 pos, vec3 to_light, vec3 normal){
	vec4 diffuse = vec4(0,0,0,0);
	vec4 spec = vec4(0,0,0,0);
	float diffuseFactor = max(dot(normal,to_light),0.0);
	diffuse = vec4(light_color, 1.0) * light_intensity * diffuseFactor;
	
	vec3 camera_dir = normalize(camera_position - pos);
	vec3 from_light = -to_light;
	vec3 reflect_light = normalize(reflect(from_light,normal));
	float specularFactor = max(dot(camera_dir, reflect_light), 0.0);
	specularFactor = pow(specularFactor, specularPower);
	spec = light_intensity * specularFactor * material.reflect * vec4(light_color,1.0);
	return (diffuse + spec);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal){
	vec3 light_dir = light.position - position;
	vec3 to_light = normalize(light_dir);
	vec4 light_color = calcLightColor(light.color, light.intensity, position, to_light, normal);
	float distance = length(light_dir);
	float attenInv = light.att.constant + light.att.linear * distance + light.att.exponent * distance * distance;
	return light_color / attenInv;
}

vec4 calcBaseColor(Material mat, vec2 texcoord){
	vec4 basecolor;
	if(material.hasTexture == 1){
		basecolor = texture(material.tex, texcoord);
	}else{
		basecolor = vec4(material.color, 1);
	}
	return basecolor;
}

vec3 calcNormal(Material material, vec3 normal, vec2 texcoord, mat4 mvMatrix){
	vec3 newNorm = normal;
	if(material.hasNormalMap){
		newNorm = texture(material.normalMap, texcoord).rgb;
		newNorm = normalize(newNorm * - 2);
		newNorm = normalize(mvMatrix * vec4(newNorm, 0.0)).xyz;
	}
	return newNorm;
}

float calcShadow(vec4 position){
	vec3 projcoords = position.xyz;
	projcoords = projcoords * 0.5 + 0.5;
	float bias = 0.05;
	float shadowFactor = 0.0;
	vec2 inc = 1.0 / textureSize(shadowMap, 0);
	for(int r=-1;r<=1;r++){
		for(int c=-1;c<=1;c++){
			float texdepth = texture(shadowMap, projcoords.xy + vec2(r,c) * inc).r;
			shadowFactor += projcoords.z - bias > texdepth ? 1.0 : 0.0;
		}
	}
	
	shadowFactor /= 9.0f;
	if(projcoords.z > 1.0){
		shadowFactor = 1;
	}
	return 1 - shadowFactor;
}

void main(){
	vec4 base = calcBaseColor(material,outTexcoord);
	vec3 norm = calcNormal(material, mvVertexNormal, outTexcoord, outModelViewMatrix);
	vec4 light = vec4(0,0,0,0);
	for(int i=0;i<MAX_POINT_LIGHTS;i++){
		if(pointLights[i].intensity > 0){
			light += calcPointLight(pointLights[i], mvVertexPos, norm);
		}
	}
	float shadow = calcShadow(mlightviewVertexPos);
	fragColor = base * (vec4(ambientLight, 1.0) + light * shadow);	
}