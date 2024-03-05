-- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  System:lv3  now:lv4  start:lv5  A:lv6  Av:lv7  
-- Atv:lv8  AtAv:lv9  N:lv10  u:lv11  v:lv12  t:lv13  vBv:lv14  vv:lv15  
local System = require(java.lang.System)
local now = System["currentTimeMillis"]
local start = now()
local A = function(i, j)
    -- fnId : 0 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  i:lv3  j:lv4  ij:lv5  
    local ij = (i + j - 1)
    return (1.0 / ((ij * (ij - 1) * 0.5) + i))
end
local Av = function(x, y, N)
    -- fnId : 1  UpValues : lv9$A <- lv6$A 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  x:lv3  y:lv4  N:lv5  A:lv9  
    for i = 1, N, 1 do
        -- i:lv6  a:lv7  
        local a = 0
        for j = 1, N, 1 do
            -- j:lv8  
            a = (a + (x[j] * A(i, j)))
        end
        y[i] = a
    end
end
local Atv = function(x, y, N)
    -- fnId : 2  UpValues : lv9$A <- lv6$A 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  x:lv3  y:lv4  N:lv5  A:lv9  
    for i = 1, N, 1 do
        -- i:lv6  a:lv7  
        local a = 0
        for j = 1, N, 1 do
            -- j:lv8  
            a = (a + (x[j] * A(j, i)))
        end
        y[i] = a
    end
end
local AtAv = function(x, y, t, N)
    -- fnId : 3  UpValues : lv7$Av <- lv7$Av, lv8$Atv <- lv8$Atv 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  x:lv3  y:lv4  t:lv5  N:lv6  Av:lv7  
-- Atv:lv8  
    Av(x, t, N)
    Atv(t, y, N)
end
local N = 5500
local u, v, t = {}, {}, {}
for i = 1, N, 1 do
    -- i:lv14  
    u[i] = 1
end
for i = 1, 10, 1 do
    -- i:lv14  
    AtAv(u, v, t, N)
    AtAv(v, u, t, N)
end
local vBv, vv = 0, 0
for i = 1, N, 1 do
    -- i:lv16  ui:lv17  vi:lv18  
    local ui, vi = u[i], v[i]
    vBv = (vBv + (ui * vi))
    vv = (vv + (vi * vi))
end
print(math["sqrt"]((vBv / vv)))
print("use", (now() - start))
