/*
 * Original shader from: https://www.shadertoy.com/view/td3cW7
 */


#ifdef GL_ES
precision lowp float;
#endif

// glslsandbox uniforms
uniform sampler2D texture;
uniform float time;
uniform vec2 resolution;
uniform float opacity;


// shadertoy emulation
#define iTime time
#define iResolution resolution

// Emulate some GLSL ES 3.x
#define round(x) (floor((x) + 0.5))

// --------[ Original ShaderToy begins here ]---------- //
// Distance functions by iq
// https://www.shadertoy.com/view/Xds3zN


float dot2( in vec3 v ) { return dot(v,v); }

float sdBox( vec3 p, vec3 b )
{
    vec3 d = abs(p) - b;
    return min(max(d.x,max(d.y,d.z)),0.0) + length(max(d,0.0));
}

float sdCapsule( vec3 p, vec3 a, vec3 b, float r )
{
    vec3 pa = p-a, ba = b-a;
    float h = clamp( dot(pa,ba)/dot(ba,ba), 0.0, 1.0 );
    return length( pa - ba*h ) - r;
}


float sdRoundCone( in vec3 p, in float r1, float r2, float h )
{
    vec2 q = vec2( length(p.xz), p.y );

    float b = (r1-r2)/h;
    float a = sqrt(1.0-b*b);
    float k = dot(q,vec2(-b,a));

    if( k < 0.0 ) return length(q) - r1;
    if( k > a*h ) return length(q-vec2(0.0,h)) - r2;

    return dot(q, vec2(a,b) ) - r1;
}


float sdRoundCone(vec3 p, vec3 a, vec3 b, float r1, float r2)
{
    // sampling independent computations (only depend on shape)
    vec3  ba = b - a;
    float l2 = dot(ba,ba);
    float rr = r1 - r2;
    float a2 = l2 - rr*rr;
    float il2 = 1.0/l2;

    // sampling dependant computations
    vec3 pa = p - a;
    float y = dot(pa,ba);
    float z = y - l2;
    float x2 = dot2( pa*l2 - ba*y );
    float y2 = y*y*l2;
    float z2 = z*z*l2;

    // single square root!
    float k = sign(rr)*rr*rr*x2;
    if( sign(z)*a2*z2 > k ) return  sqrt(x2 + z2)        *il2 - r2;
    if( sign(y)*a2*y2 < k ) return  sqrt(x2 + y2)        *il2 - r1;
    return (sqrt(x2*a2*il2)+y*rr)*il2 - r1;
}

#define PI 3.1415926
#define TAU (2.*PI)
#define smix(a,b,x) mix(a,b,0.5+0.5*(x))
#define ROT(t) mat2(cos(t), sin(t), -sin(t), cos(t))

// ------------
// WALKING TRIP
// ------------

// Alexis THIBAULT, 09/2020



// Most measurements used come from these documents:
// https://upload.wikimedia.org/wikipedia/commons/8/82/Drawing_of_proportions_of_the_male_and_female_figure%2C_1936.jpg
// https://thegenealogyofstyle.files.wordpress.com/2014/12/marey_morin20walk_1886.jpg

// But there was also a lot of tweaking, of course.
// Thank you iq for this "rounded cone" sdf, I love it!

float smin(float a, float b, float k)
{
    float h = clamp(1.-abs((b-a)/k), 0., 2.);
    return min(a,b) - k*0.25*h*h*step(-1.,-h);
}
float smax(float a, float b, float k)
{
    float h = clamp(1.-abs((b-a)/k), 0., 2.);
    return max(a,b) + k*0.25*h*h*step(-1.,-h);
}

/*
float smin(float a, float b, float k, float d)
{
    // Modified smooth-min function
    float h = clamp(1.-abs((b-a)/k), 0.,2.);
    return min(a,b) - k/d*0.5*pow(h,d) * step(-1.,-h);
}
*/

float gain(float x, float d)
{
    // Modified gain function, for x between -1 and 1
    return (1.-pow(1.-abs(x), d)) * sign(x);
}



float halfMan( vec3 p, float t )
{
    // Only the right half of the man (y < 0.)
    // (yeah sorry I'm the kind of person who uses vertical z)
    // Measurements are in meters

    // TODO Improve hand and foot
    float d;

    // Torso
    #if 0
    // Old version
    d = sdRoundCone(p, vec3(0.03, -0.07,-0.1), vec3(-0.01,-0.095,-0.27), 0.085, 0.10);
    float srad = clamp(0.5*(0.13-p.x), 0., 0.1);
    d = smin(d, sdRoundCone(p, vec3(0.02, -0.07,-0.1), vec3(-0.02, -0.08, 0.15), 0.085, 0.125), srad);
    #else
    // Re-modeled, and with animation
    vec3 hip = vec3(-0.01-0.02*sin(PI*t),-0.095,-0.27);
    d = sdRoundCone(p, vec3(0.02, -0.07,-0.1), hip, 0.08, 0.10);
    float srad = 0.1*smoothstep(0.,0.1,0.095-0.5*p.x);
    vec3 shoulder = vec3(-0.02+0.01*sin(PI*t+PI/4.), -0.08, 0.15);
    d = smin(d, sdRoundCone(p, vec3(0.02, -0.07,-0.02), shoulder, 0.085, 0.125), srad);
    #endif

    // Femur
    float th2M = TAU/12., th2m = -TAU*13./360.;
    float th2 = smix(th2M,th2m,sin(PI*t));
    float femurL = 0.46;
    //vec3 q = p - vec3(0., -0.11, -0.275);
    vec3 q = p - hip;
    q.xz *= ROT(th2);
    q.yz *= ROT(0.07);
    q.z += femurL;
    d = smin(d, sdRoundCone(q.xzy, 0.06, 0.095, 0.4), 0.03);

    // Tibia
    float th1M = -0.01, th1m = -TAU/6.;
    float th1 = mix(th1M,th1m,pow(0.5+0.5*sin(PI*t-TAU/4.),2.));
    float tibiaL = 0.355;
    q.y += 0.005;
    q.xz *= mat2(cos(th1), sin(th1), -sin(th1), cos(th1));
    q.yz *= ROT(-0.05);
    q.z += tibiaL;
    d = smin(d, sdRoundCone(q.xzy, 0.041, 0.055, 0.31), 0.02);

    // Foot
    float th0set=-th1-th2, th0relax=0.5*th1+0.1;
    //float th0 = mix(th0set, th0relax, pow(0.5+0.5*sin(PI*t-TAU/4.), 3.0));
    float th0 = mix(th0set, th0relax, 0.5+0.5*gain(sin(PI*t-TAU/3.), 3.0));
    vec3 foot = vec3(0.25,0.11,0.035);
    q.xz *= mat2(cos(th0),sin(th0),-sin(th0),cos(th0));
    q.z += 0.065;
    q.x -= 0.05;
    d = smin(d, sdBox(q,0.5*foot-0.02)-0.02, 0.1);

    // Upper arm
    q = p - vec3(-0.03+shoulder.x, -0.19, 0.215);
    float uarmL = 0.305;
    float th3 = 0.3*sin(PI*t);
    q.xz *= mat2(cos(th3), sin(th3), -sin(th3), cos(th3));
    q.yz *= ROT(-0.25);
    q.z += uarmL;
    srad = clamp(0.8*(p.z-0.16)-0.3*(p.x+0.05), 0., 0.1);
    d = smin(d, sdRoundCone(q.xzy, 0.045, 0.07, uarmL), srad);

    // Lower arm
    float larmL = 0.254;
    float th4 = 0.15+0.15*sin(PI*t-PI/6.);
    q.xz *= ROT(th4);
    q.yz *= ROT(0.15);
    q.z += larmL;
    d = smin(d, sdRoundCone(q.xzy, 0.03, 0.045, 0.21), 0.02);

    // Hand
    float phi5 = PI/6.;
    q.xz *= ROT(0.1+0.01*sin(PI*t-PI/4.));
    q.xy *= ROT(phi5);
    q.z += 0.08;
    q.y += 0.008;
    d = smin(d, sdBox(q, vec3(0.04, 0.02,0.08)-0.02)-0.025, 0.0);
    d = smin(d, sdRoundCone(q, vec3(0.02,0.005,0.06), vec3(0.05,0.04,-0.03),0.025,0.01), srad);

    // Neck bottom
    d = smin(d, sdRoundCone(p, vec3(-0.04,-0.165,0.275), vec3(-0.02,0.05,0.32), 0.01,0.02), 0.05);

    return d;
}

float fullMan( vec3 p, float t )
{
    p.z -= 0.03*sin(TAU*t+PI/6.);

    // SDF cheat: don't evaluate the complicated sdf if we're too far
    float d0 = sdBox(p, vec3(0.7,0.5,1.2));
    if(d0 > 0.5) return d0-0.25;

    float srad = smoothstep(0.,1.,10.0*(0.3+p.z)+3.*p.x)*0.1 + 0.01;
    float d = smin(halfMan(p,t), halfMan(vec3(p.x,-p.y,p.z), t+1.), srad);
    // Neck
    d = smin(d, sdRoundCone(p, vec3(-0.04,0.,0.275), vec3(0.0,0.,0.38),0.06,0.06), 0.05);


    // Head
    vec3 q = p;
    q.z -= 0.35;
    q.xy *= ROT(gain(cos(0.4*t), 2.));
    q.xz *= ROT(-0.3-0.1*sin(TAU*t+PI/4.));
    q.z += 0.35;
    q = vec3(q.x,abs(q.y),q.z);
    float d2 = smin(
    sdRoundCone(q, vec3(0.,0.,0.50), vec3(0.08,   0.0,0.38),0.1,0.04),
    length(q-vec3(0.02,0.035,0.45))-0.05,
    0.04
    );
    // Eye orbit
    d2 = smax(d2, 0.01-length(q-vec3(0.1,0.05,0.46)), 0.055);
    // Nose
    d2 = smin(d2, length(q-vec3(0.11,0.,0.435)) - 0.01, 0.04);

    d = smin(d,d2,0.05);
    return d;
}

float buildings( vec3 p, float t )
{
    p.y = abs(p.y)+3.;
    p.y -= max(10.*round(p.y/10.), 10.);

    p.x = mod(p.x + 1.0*t, 30.0) - 15.;
    return sdBox(p, vec3(3.,2.5,4.))-0.1;
}

float map( vec3 p )
{
    float d = p.z+1.13;
    //d = smin(d, 2.6-length(p), 1.5);
    float t = 1.5*iTime;


    for (float row = 0.0; row < 3.0; ++row) {
        for (float col = 0.0; col < 5.0; ++col) {
            d = min(d, fullMan(p + vec3(col - 1.0, (row - 1.0) * 1.0 + sin(col) * 0.1, .0), t + row * 3.5 + col * 0.3));
        }
    }

    //    d = min(d, fullMan(p, t));
    //    d = min(d, fullMan(p + vec3(1.0, -1.0, 0.0), t + 0.22));
    //    d = min(d, fullMan(p + vec3(0.0, 1.0, 0.0), t + 0.2));
    d = min(d, buildings(p+vec3(0.,0.,1.13), t));
    return d;
}



vec3 normal( vec3 p )
{
    vec2 e = 0.001 * vec2(1, -1);
    return normalize(
    e.xxx * map(p+e.xxx)
    + e.xyy * map(p+e.xyy)
    + e.yxy * map(p+e.yxy)
    + e.yyx * map(p+e.yyx)
    );
}

// Ambient Occlusion computation stolen from iq
// https://www.shadertoy.com/view/Xds3zN
float calcAO( in vec3 pos, in vec3 nor, float scale )
{
    float occ = 0.0;
    float sca = 1.0;
    for( int i=0; i<5; i++ )
    {
        float h = 0.01 + scale*0.12*float(i)/4.0;
        float d = map( pos + h*nor );
        occ += (h-d)/scale*sca;
        sca *= 0.95;
        if( occ>0.5 ) break;
    }
    return clamp( 1.0 - 2.0*occ, 0.0, 1.0 ) * (0.5+0.5*nor.z);
}


void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = (2.0*fragCoord - iResolution.xy)/iResolution.y;
    float th = iTime * 0.1;
    vec3 ro = vec3(2.5*cos(th), 2.5*sin(th), 0.0);
    //vec3 ro = vec3(0.,-2.5,0.);
    vec3 camFwd = normalize(vec3(0) - ro);
    vec3 camRight = normalize(cross(camFwd, vec3(0,0,1)));
    vec3 camUp = cross(camRight, camFwd);
    float fov = 0.5;
    vec3 rd = (camFwd + fov * (uv.x * camRight + uv.y * camUp));
    rd = normalize(rd);

    float d, t=0.;
    for(int i=0; i<256; i++)
    {
        d = map(ro+t*rd);
        if(d < 0.001 || t > 100.) break;
        t += d;
    }
    vec3 p = ro+t*rd;
    vec3 col;
    if(t > 100.)
    {
        col = 0.5+0.5*rd;
    }
    else
    {
        vec3 n = normal(p);
        col = 1.7*(0.5+0.5*n);

        vec3 q = 10.*p; // Grid every 10cm
        vec3 grid = abs(q-round(q));
        //col = mix(col, vec3(2.0), smoothstep(0.1,0.0,min(grid.y,grid.z)));
        float sca = clamp(length(p), 1.0, 10.0);
        col *= calcAO(p,n,sca);
    }


    col *= smoothstep(3.5,1.0,length(uv));

    col = pow(col * 0.7, vec3(1./2.2));
    fragColor = vec4(col, opacity);
}
// --------[ Original ShaderToy ends here ]---------- //

void main(void)
{
float alpha =texture2D(texture, gl_TexCoord[0].xy).a;
if (alpha != 0) {

    mainImage(gl_FragColor, gl_FragCoord.xy);
}
}