#version 330

const int MAX_POINT_LIGHTS = 4;
const int MAX_SPOT_LIGHTS = 4;

in vec2 texCoord0;
in vec3 normal0;
in vec3 worldPos0;

out vec4 fragColor;


struct BaseLight{
	vec3 color;
	float intensity;
};

struct DirectionalLight{
	BaseLight base;
	vec3 direction;
};

struct Attenuation{
 	//This works for if effects like in which may be pixel shines from miles away
	//or they are shining closer or fading out with distance...
	float constant;
	float linear;
	float exponent;
};

struct PointLight{
	BaseLight base;
	Attenuation atten;
	vec3 position;
	float range;
};

struct SpotLight{
	PointLight pointLight;
	vec3 direction;
	float cutoff;
};


uniform vec3 baseColor;
uniform vec3 ambientLight;
uniform sampler2D sampler;
uniform vec3 eyePos;

uniform float specularIntensity;
uniform float specularPower;

uniform DirectionalLight directionalLight;
uniform PointLight pointLight[MAX_POINT_LIGHTS];
uniform SpotLight spotLight[MAX_SPOT_LIGHTS];


vec4 calcLight(BaseLight base, vec3 direction, vec3 normal){
	float diffuseFactor = dot(normal, -direction);

	vec4 diffuseColor = vec4(0,0,0,0);
	vec4 specularColor = vec4(0,0,0,0);

	//if diffusion factor is not effcting on our suface than... 
	if(diffuseFactor > 0){
		diffuseColor = vec4(base.color, 1.0) * base.intensity * diffuseFactor;

		//Here 2 calc are done of vectors 1 of them is between our eye and camera and 
		//other is the direction of light refelects of the surface so we are gonna compare
		//these 2 vectors so the closer they are the more reflection we get...

		vec3 directionToEye = normalize(eyePos - worldPos0);
		//reflect is glsl pre-defined function...
		vec3 refelectDirection = normalize(reflect(direction, normal));

		float specularFactor = dot(directionToEye, refelectDirection);
		specularFactor = pow(specularFactor, specularPower);

		if(specularFactor > 0){
			specularColor = vec4(base.color, 1.0) * specularIntensity * specularFactor;
		}
	}
	return diffuseColor + specularColor;
}

vec4 calcDirectionalLight(DirectionalLight directionalLight, vec3 normal){
	return calcLight(directionalLight.base, -directionalLight.direction, normal);
}

vec4 calcPointLight(PointLight pointLight, vec3 normal){
	vec3 lightDirection = worldPos0 - pointLight.position;
	float distanceToPoint = length(lightDirection);

	if(distanceToPoint > pointLight.range)
		return vec4(0,0,0,0);

	lightDirection = normalize(lightDirection);

	vec4 color = calcLight(pointLight.base, lightDirection, normal);

	float attenuation = pointLight.atten.constant +
	 pointLight.atten.linear * distanceToPoint +
	 pointLight.atten.exponent * distanceToPoint * distanceToPoint + 
	 0.0001f;
	//we add 0.0001f because in we cannot divide by 0 and if we but condition for it glsl sometimes true both conditions...
	return color / attenuation;
}

vec4 calcSpotLight(SpotLight spotLight, vec3 normal){

	vec3 lightDirection = normalize(worldPos0 - spotLight.pointLight.position);
	float spotFactor = dot(lightDirection, spotLight.direction);

	vec4 color = vec4(0,0,0,0);

	if(spotFactor > spotLight.cutoff){
		color = calcPointLight(spotLight.pointLight, normal) * 
		 (1.0 - (1.0 - spotFactor) / (1.0 - spotLight.cutoff));
	}
	return color;
}

void main(){
	vec4 totalLight  = vec4(ambientLight, 1);
	vec4 color = vec4(baseColor, 1);
	vec4 textureColor = texture(sampler, texCoord0.xy);

	if(textureColor != vec4(0,0,0,0))
		color *= textureColor; 


	vec3 normal = normalize(normal0);

	totalLight += calcDirectionalLight(directionalLight, normal);

	for(int i = 0; i < MAX_POINT_LIGHTS; i++){
		if(pointLight[i].base.intensity > 0)
				totalLight += calcPointLight(pointLight[i], normal);

	for(int i = 0; i < MAX_SPOT_LIGHTS; i++){
		if(spotLight[i].pointLight.base.intensity > 0)
				totalLight += calcSpotLight(spotLight[i], normal);
	}

	fragColor = color * totalLight;
    }
}