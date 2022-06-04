uniform sampler2D texture;
uniform float elapsedTime;
uniform float timeLastHurt;
uniform float isGlint;
uniform float isPaletteSwapped;

void main()
{
	vec4 pixel = texture2D(texture, gl_TexCoord[0].xy);
	const float strength = 0.8;
	const float halfCyclesPerSecond = 6.0;
	float hurtFlashMultiplier = abs(sin((elapsedTime - timeLastHurt) * halfCyclesPerSecond));
	float glintMultiplier = abs(sin(elapsedTime * 1));
	
	if (isPaletteSwapped > 0.1)
	{
		pixel.r = pixel.g;
	}
	
	if (isGlint > 0.1)
	{
		pixel = mix(pixel, vec4(.5,.5,0.,1.), glintMultiplier * strength * pixel.a);
	}
	
	if (elapsedTime - timeLastHurt <= 1. && elapsedTime - timeLastHurt >= 0)
	{
		pixel = mix(pixel, vec4(1.,0.,0.,1.), hurtFlashMultiplier * strength * pixel.a);
	}
	gl_FragColor = pixel;
} 