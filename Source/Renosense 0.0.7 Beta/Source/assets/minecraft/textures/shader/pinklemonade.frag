#extension GL_OES_standard_derivatives : enable

precision highp float;

uniform sampler2D texture;
uniform float time;
uniform vec2 resolution;
uniform float opacity;


float random (in vec2 st) {
    return fract(sin(dot(st.xy,
    vec2(12.9898, 78.233)))*
    43758.5453123);
}

// Based on Morgan McGuire @morgan3d
// https://www.shadertoy.com/view/4dS3Wd
float noise (in vec2 st) {
    vec2 i = floor(st);
    vec2 f = fract(st);

    // Four corners in 2D of a tile
    float a = random(i);
    float b = random(i + vec2(1.0, 0.0));
    float c = random(i + vec2(0.0, 1.0));
    float d = random(i + vec2(1.0, 1.0));

    vec2 u = f * f * (3.0 - 2.0 * f);

    return mix(a, b, u.x) +
    (c - a)* u.y * (1.0 - u.x) +
    (d - b) * u.x * u.y;
}

mat2 rotate2d(float _angle){

    return mat2(cos(_angle), -sin(_angle), sin(_angle), cos(_angle));
}

#define OCTAVES 7
float fbm(in vec2 st) {
    // Initial values
    float value = 0.;
    float amplitude = 0.5;
    vec2 shift = vec2(0.1);
    mat2 rot = rotate2d(0.5);

    // Loop of octaves
    for (int i = 0; i < OCTAVES; i++) {
        st = rot*st * 2.4 + shift;
        value += amplitude * noise(st);
        amplitude *= 0.5661314;//云朵的那个什么

    }
    return value;
}

void main(void) {
    float alpha =texture2D(texture, gl_TexCoord[0].xy).a;
    if (alpha != 0) {

        // 改改color罢了
        vec2 st = gl_FragCoord.xy/resolution.xy;
        st.x *= resolution.x/resolution.y;
        vec3 color = vec3(0.0);
        //"folded" texture
        vec2 q = vec2(0.);
        q.x = fbm(gl_FragCoord.xy/resolution.xy+time*0.01);
        q.y = fbm(gl_FragCoord.xy/resolution.xy+time*0.01);
        // preview q
        color += vec3(q.x, 0., 0.);

        vec2 r = vec2(0.);
        r.x = fbm(st+q+time*0.05);
        r.y = fbm(st+q+vec2(.4, 0.7)+time*0.08);
        // preview r
        color += vec3(r.x, r.y, 0.);

        float f = fbm(st+r);
        //preview f
        color += vec3(q.x, 1., 1.);

        color = mix(vec3(9.6, 0.9255, 0.9478),
        vec3(0.1255, 0.2784, 1.9478),
        1.0);
        color = mix(color,
        vec3(0.0157, 0.3294, 1.2667),
        //vec3(0.5 + 0.5*sin(time+st.yxx+vec3(0,2,4))),
        1.0);
        color = mix(vec3(0.9, 0.1, 0.4),
        vec3(0.8029, 0.8029, 0.8055),
        //vec3(0.5 + 0.5*sin(time+st.yyx+vec3(0,2,4))),
        clamp(length(r.x), 0.0, 1.0));

        gl_FragColor = vec4(color, opacity);

    }
}