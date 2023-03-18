// someone please make this shader with ones and zeros
#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D texture;
uniform float time;
uniform vec2 resolution;
uniform float opacity;


void main(void)
{
    float alpha =texture2D(texture, gl_TexCoord[0].xy).a;
    if (alpha != 0) {
        vec2 uv = (gl_FragCoord.xy / resolution.xy) * 2.0 - 1.0;
        uv.x *= resolution.x/resolution.y;

        vec3 finalColor = vec3(0.0, 0.0, 0.0);

        float g = -mod(gl_FragCoord.y + time, cos(gl_FragCoord.x) + 0.004);
        g = g + clamp(uv.y, -0.3, 0.0);

        finalColor = vec3(0.0, g, 0.0);

        gl_FragColor = vec4(finalColor, opacity);
    }
}