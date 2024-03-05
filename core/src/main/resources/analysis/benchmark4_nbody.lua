-- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  sqrt:lv3  PI:lv4  SOLAR_MASS:lv5  DAYS_PER_YEAR:lv6  bodies:lv7  
-- advance:lv8  energy:lv9  offsetMomentum:lv10  N:lv11  nbody:lv12  
local sqrt = math["sqrt"]
local PI = 3.141592653589793
local SOLAR_MASS = (4 * PI * PI)
print(SOLAR_MASS)
local DAYS_PER_YEAR = 365.24
local bodies = {[1]={["x"]=0;["y"]=0;["z"]=0;["vx"]=0;["vy"]=0;["vz"]=0;["mass"]=SOLAR_MASS;};[2]={["x"]=4.841431442464721;["y"]=-1.1603200440274284;["z"]=-0.10362204447112311;["vx"]=(0.001660076642744037 * DAYS_PER_YEAR);["vy"]=(0.007699011184197404 * DAYS_PER_YEAR);["vz"]=(- (6.90460016972063E-5 * DAYS_PER_YEAR));["mass"]=(9.547919384243266E-4 * SOLAR_MASS);};[3]={["x"]=8.34336671824458;["y"]=4.124798564124305;["z"]=-0.4035234171143214;["vx"]=(- (0.002767425107268624 * DAYS_PER_YEAR));["vy"]=(0.004998528012349172 * DAYS_PER_YEAR);["vz"]=(2.3041729757376393E-5 * DAYS_PER_YEAR);["mass"]=(2.858859806661308E-4 * SOLAR_MASS);};[4]={["x"]=12.894369562139131;["y"]=-15.111151401698631;["z"]=-0.22330757889265573;["vx"]=(0.002964601375647616 * DAYS_PER_YEAR);["vy"]=(0.0023784717395948095 * DAYS_PER_YEAR);["vz"]=(- (2.9658956854023756E-5 * DAYS_PER_YEAR));["mass"]=(4.366244043351563E-5 * SOLAR_MASS);};[5]={["x"]=15.379697114850917;["y"]=-25.919314609987964;["z"]=0.17925877295037118;["vx"]=(0.0026806777249038932 * DAYS_PER_YEAR);["vy"]=(0.001628241700382423 * DAYS_PER_YEAR);["vz"]=(- (9.515922545197159E-5 * DAYS_PER_YEAR));["mass"]=(5.1513890204661145E-5 * SOLAR_MASS);};}
local advance = function(bodies, nbody, dt)
    -- fnId : 0  UpValues : lv21$sqrt <- lv3$sqrt 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  bodies:lv3  nbody:lv4  dt:lv5  sqrt:lv21  
    for i = 1, nbody, 1 do
        -- i:lv6  bi:lv7  bix:lv8  biy:lv9  biz:lv10  bimass:lv11  bivx:lv12  bivy:lv13  
        -- bivz:lv14  
        local bi = bodies[i]
        local bix, biy, biz, bimass = bi["x"], bi["y"], bi["z"], bi["mass"]
        local bivx, bivy, bivz = bi["vx"], bi["vy"], bi["vz"]
        for j = (i + 1), nbody, 1 do
            -- j:lv15  bj:lv16  dx:lv17  dy:lv18  dz:lv19  mag:lv20  bm:lv22  
            local bj = bodies[j]
            local dx, dy, dz = (bix - bj["x"]), (biy - bj["y"]), (biz - bj["z"])
            local mag = sqrt(((dx * dx) + (dy * dy) + (dz * dz)))
            mag = (dt / (mag * mag * mag))
            local bm = (bj["mass"] * mag)
            bivx = (bivx - (dx * bm))
            bivy = (bivy - (dy * bm))
            bivz = (bivz - (dz * bm))
            bm = (bimass * mag)
            bj["vx"] = (bj["vx"] + (dx * bm))
            bj["vy"] = (bj["vy"] + (dy * bm))
            bj["vz"] = (bj["vz"] + (dz * bm))
        end
        bi["vx"] = bivx
        bi["vy"] = bivy
        bi["vz"] = bivz
        bi["x"] = (bix + (dt * bivx))
        bi["y"] = (biy + (dt * bivy))
        bi["z"] = (biz + (dt * bivz))
    end
end
local energy = function(bodies, nbody)
    -- fnId : 1  UpValues : lv18$sqrt <- lv3$sqrt 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  bodies:lv3  nbody:lv4  e:lv5  sqrt:lv18  
    local e = 0
    for i = 1, nbody, 1 do
        -- i:lv6  bi:lv7  vx:lv8  vy:lv9  vz:lv10  bim:lv11  
        local bi = bodies[i]
        local vx, vy, vz, bim = bi["vx"], bi["vy"], bi["vz"], bi["mass"]
        e = (e + (0.5 * bim * ((vx * vx) + (vy * vy) + (vz * vz))))
        for j = (i + 1), nbody, 1 do
            -- j:lv12  bj:lv13  dx:lv14  dy:lv15  dz:lv16  distance:lv17  
            local bj = bodies[j]
            local dx, dy, dz = (bi["x"] - bj["x"]), (bi["y"] - bj["y"]), (bi["z"] - bj["z"])
            local distance = sqrt(((dx * dx) + (dy * dy) + (dz * dz)))
            e = (e - ((bim * bj["mass"]) / distance))
        end
    end
    return e
end
local offsetMomentum = function(b, nbody)
    -- fnId : 2  UpValues : lv5$SOLAR_MASS <- lv5$SOLAR_MASS 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  b:lv3  nbody:lv4  SOLAR_MASS:lv5  px:lv6  py:lv7  
-- pz:lv8  
    print(SOLAR_MASS)
    local px, py, pz = 0, 0, 0
    for i = 1, nbody, 1 do
        -- i:lv9  bi:lv10  bim:lv11  
        local bi = b[i]
        local bim = bi["mass"]
        px = (px + (bi["vx"] * bim))
        py = (py + (bi["vy"] * bim))
        pz = (pz + (bi["vz"] * bim))
    end
    print(SOLAR_MASS)
    b[1]["vx"] = (- (px / SOLAR_MASS))
    b[1]["vy"] = (- (py / SOLAR_MASS))
    b[1]["vz"] = (- (pz / SOLAR_MASS))
end
local N = (tonumber((arg and arg[1])) or 1000)
local nbody = (# bodies)
offsetMomentum(bodies, nbody)
io["write"](string["format"]("%0.9f", energy(bodies, nbody)), "\n")
for i = 1, N, 1 do
    -- i:lv13  
    advance(bodies, nbody, 0.01)
end
io["write"](string["format"]("%0.9f", energy(bodies, nbody)), "\n")
