-- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  System:lv3  now:lv4  empty:lv5  start:lv6  count:lv7  
local System = require(java.lang.System)
local now = System["currentTimeMillis"]
print("now is", now())
print("start")
print(now)
local empty = function()
    -- fnId : 0 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  
    return 
end
local start = now()
local count = (1000 * 1000 * 100)
for j = 0, count, 1 do
    -- j:lv8  
    empty()
end
total = (now() - start)
print(total)
