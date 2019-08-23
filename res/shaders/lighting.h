uniform vec3 C_eyePos;
uniform float specularIntensity;
uniform float specularPower;

struct BaseLight
{
	vec3 color;
	float intensity;
};

struct Attenuation
{
 	//This works for if effects like in which may be pixel shines from miles away
	//or they are shining closer or fading out with distance...
	float constant;
	float linear;
	float exponent;
};

struct PointLight
{
	BaseLight base;
	Attenuation atten;
	vec3 position;
	float range;
};

struct SpotLight
{
	PointLight pointLight;
	vec3 direction;
	float cutoff;
};

struct DirectionalLight
{
	BaseLight base;
	vec3 direction;
};

vec4 calcLight(BaseLight base, vec3 direction, vec3 normal, vec3 worldPos){
	float diffuseFactor = dot(normal, -direction);

	vec4 diffuseColor = vec4(0,0,0,0);
	vec4 specularColor = vec4(0,0,0,0);

	//if diffusion factor is not effcting on our suface than... 
	if(diffuseFactor > 0){
		diffuseColor = vec4(base.color, 1.0) * base.intensity * diffuseFactor;

		//Here 2 calc are done of vectors 1 of them is between our eye and camera and 
		//other is the direction of light refelects of the surface so we are gonna compare
		//these 2 vectors so the closer they are the more reflection we get...

		vec3 directionToEye = normalize(C_eyePos - worldPos);
		//reflect is glsl pre-defined function...
		
		//vec3 refelectDirection = normalize(reflect(direction, normal));
		vec3 halfDirection = normalize(directionToEye - direction);
//Changing lighting model.
		float specularFactor = dot(halfDirection, normal);
		//float specularFactor = dot(directionToEye, refelectDirection);
		
		specularFactor = pow(specularFactor, specularPower);

		if(specularFactor > 0){
			specularColor = vec4(base.color, 1.0) * specularIntensity * specularFactor;
		}
	}
	return diffuseColor + specularColor;
}

vec4 calcPointLight(PointLight pointLight, vec3 normal, vec3 worldPos){
	vec3 lightDirection = worldPos - pointLight.position;
	float distanceToPoint = length(lightDirection);

	if(distanceToPoint > pointLight.range)
		return vec4(0,0,0,0);

	lightDirection = normalize(lightDirection);

	vec4 color = calcLight(pointLight.base, lightDirection, normal, worldPos);

	float attenuation = pointLight.atten.constant +
	 pointLight.atten.linear * distanceToPoint +
	 pointLight.atten.exponent * distanceToPoint * distanceToPoint + 
	 0.0001f;
	//we add 0.0001f because in we cannot divide by 0 and if we but condition for it glsl sometimes true both conditions...
	return color / attenuation;
}

vec4 calcSpotLight(SpotLight spotLight, vec3 normal, vec3 worldPos){

	vec3 lightDirection = normalize(worldPos - spotLight.pointLight.position);
	float spotFactor = dot(lightDirection, spotLight.direction);

	vec4 color = vec4(0,0,0,0);

	if(spotFactor > spotLight.cutoff){
		color = calcPointLight(spotLight.pointLight, normal, worldPos) * 
		 (1.0 - (1.0 - spotFactor) / (1.0 - spotLight.cutoff));
	}
	return color;
}

vec4 calcDirectionalLight(DirectionalLight directionalLight, vec3 normal, vec3 worldPos){
	return calcLight(directionalLight.base, -directionalLight.direction, normal, worldPos);
}