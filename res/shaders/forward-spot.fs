#version 120
#include "lighting.h"

varying vec2 texCoord0;
varying vec3 normal0;
varying vec3 worldPos0;

uniform sampler2D diffuse;
uniform SpotLight R_spotLight;


void main(){

	gl_FragColor = texture2D(diffuse, texCoord0.xy) * calcSpotLight(R_spotLight, normalize(normal0), worldPos0);
	//syntax of glsl 330
	//fragColor = color * totalLight;
}