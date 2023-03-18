// shay der toy: https://www.shadertoy.com/view/ctXXWn

#extension GL_OES_standard_derivatives : enable

precision highp float;

uniform sampler2D texture;
uniform float time;
uniform vec2 resolution;
uniform float opacity;


#define saturate(x) clamp(x,0, 1.4)
#define smooth(x) smoothstep(0., 1., x)

#define PERIOD 10.

float pulse(float x, float k)
{
    return 1.-min(1., abs(fract(x)-0.5)/k);
}

mat2 r(float t)
{
    return mat2(cos(t), -sin(t), sin(t), cos(t));
}

vec2 hash22(vec2 p)
{
    vec3 p3 = fract(p.xyx * vec3(.1031, .1030, .0973));
    p3 += dot(p3, p3.yzx+33.33);
    return fract((p3.xx+p3.yz)*p3.zy);
}

float perlinNoise(vec2 p, float t)
{
    mat2 r = r(t);

    vec2 tlVal = r*(hash22(vec2(floor(p.x),  ceil(p.y))) - 0.5) * 2.0;
    vec2 blVal = r*(hash22(vec2(floor(p.x), floor(p.y))) - 0.5) * 2.0;
    vec2 trVal = r*(hash22(vec2( ceil(p.x),  ceil(p.y))) - 0.5) * 2.0;
    vec2 brVal = r*(hash22(vec2( ceil(p.x), floor(p.y))) - 0.5) * 2.0;

    float tl = dot(p - vec2(floor(p.x),  ceil(p.y)), tlVal);
    float bl = dot(p - vec2(floor(p.x), floor(p.y)), blVal);
    float tr = dot(p - vec2( ceil(p.x),  ceil(p.y)), trVal);
    float br = dot(p - vec2( ceil(p.x), floor(p.y)), brVal);

    float noiseVal = mix(mix(bl, tl, smooth(fract(p.y))),
    mix(br, tr, smooth(fract(p.y))),
    smooth(fract(p.x)))*0.5 + 0.5;

    // Add color to the noise
    vec3 col = vec3(1.0, 0.5, 0.2);
    return mix(mix(bl, tl, smooth(fract(p.y))),
    mix(br, tr, smooth(fract(p.y))),
    smooth(fract(p.x)))*0.5 + 0.5;
}

float vignette(vec2 p, float s)
{
    p = 1.-(4.*p*p-4.*p+1.)*s;
    return p.x*p.y;
}

void main( void ) {
float alpha =texture2D(texture, gl_TexCoord[0].xy).a;
if (alpha != 0) {
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = (gl_FragCoord.xy*2.-resolution.xy)/resolution.y;

    float v = perlinNoise(uv*2.+time*0.05, time*0.2);
    float fw = fwidth(v);
    float t = 0.8;
    vec3 col = (smoothstep(t, t+fw*PERIOD*2., pulse(v*PERIOD, 1.))*0.8+0.2)*vec3(0.851, 0.400, 1.000);

    // Output to screen
    gl_FragColor = vec4(col*vignette(gl_FragCoord.xy/resolution.xy, 0.3), opacity);
}
}