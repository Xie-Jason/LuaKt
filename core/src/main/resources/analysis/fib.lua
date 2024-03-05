-- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  now:lv3  start:lv4  num:lv5  
print("Hello fib")
System = require(java.lang.System)
local now = System["currentTimeMillis"]
local start = now()
fib = function(n)
    -- fnId : 0 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  n:lv3  
    if ((n == 1) or (n == 2)) then
        return 1
    else
        return (fib((n - 1)) + fib((n - 2)))
    end
end
local num = (tonumber(arg[0]) or 30)
print(("数字" .. num .. "的fib结果为"), fib(num))
print(("用时" .. (now() - start) .. "ms"))
