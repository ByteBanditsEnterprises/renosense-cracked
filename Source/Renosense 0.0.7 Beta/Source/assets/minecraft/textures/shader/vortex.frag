#ifdef GL_ES
precision mediump float;
#endif

#extension GL_OES_standard_derivatives : enable

uniform sampler2D texture;
uniform float time;
uniform vec2 resolution;

uniform float opacity;


void main( void )
{
float alpha =texture2D(texture, gl_TexCoord[0].xy).a;
if (alpha != 0) {
    vec2 position = (gl_FragCoord.xy * 2.0 -  resolution) / min(resolution.x, resolution.y);
    vec3 destColor = vec3(1.2, .4, .6);
    float f = 0.0;

    for (float i = 0.0; i < 111.0; i++)
    {
        float s = 0.2*sin(0.11*time + i);
        float c = 1.2*cos(0.02*time + i);
        f += 0.0015 / abs(length(11.0* position * f - vec2(c, s)) - 0.8);
    }

    gl_FragColor = vec4(vec3(destColor * f), opacity);
}
}