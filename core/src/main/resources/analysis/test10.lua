-- import test11.lua 
-- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  getH:lv3  count:lv4  n:lv5  i:lv6  j:lv7  
-- k:lv8  getTwo:lv9  needThree:lv10  n1:lv11  n2:lv12  f:lv13  c:lv14  
local getH = require(test11.lua)
print(getH())
local count = 5
for i = 0, 5, 1 do
    -- i:lv5  getLoopIdx:lv6  
    count = (count - 1)
    local getLoopIdx = function()
        -- fnId : 0  UpValues : lv3$i <- lv5$i, lv4$count <- lv4$count 
        -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  i:lv3  count:lv4  
        print(i, "+", count, "=", (i + count))
    end
    getLoopIdx()
end
local n, i, j, k = 10, 20, 30, 40, 50
print(n, i, j, k)
local getTwo = function()
    -- fnId : 1 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  
    return 114, 514
end
local needThree = function(n1, n2, n3)
    -- fnId : 2 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  n1:lv3  n2:lv4  n3:lv5  
    print("needThree -> ", n1, n2, n3)
end
needThree(getTwo())
needThree(getTwo(), 22, 33)
local n1, n2 = 10, 20
local f = function(j, k)
    -- fnId : 3 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  j:lv3  k:lv4  
    if (j < k) then
        print((j .. " < " .. k))
    elseif (j > k) then
        print((j .. " > " .. k))
    else
        print((j .. " = " .. k))
    end
end
f(n1, n2)
f(n2, n1)
f(n1, n1)
f(n2, n2)
local c = 5
repeat
    c = (c - 1)
until (c > 0)
print(c, "is a", type(c))
print((c + "11222"))
while (c < 10) do
    c = (c + 1)
    print("value is ", c)
end
Integer = require(java.lang.Integer)
print(Integer["MAX_VALUE"])
print((Integer["valueOf"]("22222") + 11))
print(Integer["valueOf"]("22222")["equals"](Integer["valueOf"]("22222"), 22222))
print((Integer["valueOf"]("22222") == 22222))
Long = require(java.lang.Long)
print(Long["valueOf"]("11")["equals"](Long["valueOf"]("11"), 11))
ArrayList = require(java.util.ArrayList)
arr = ArrayList(10)
print(arr, type(arr))
arr["add"](arr, 11)
arr["add"](arr, "www")
arr["add"](arr, 1, "↓")
print(arr)
System = require(java.lang.System)
print(System["currentTimeMillis"]())
print("complete")
