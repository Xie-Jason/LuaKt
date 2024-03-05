-- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  flag:lv3  value:lv4  num:lv5  hello:lv6  log:lv7  
-- f:lv8  
local flag = false
local value, num = 11, 22
print(arg[0])
local hello = function(n)
    -- fnId : 0 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  n:lv3  
    print((n * 5))
    return "Hello"
end
local log = function(fn, ...)
    -- fnId : 1 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  fn:lv3  ...:lv4  res:lv5  
    print("before exec")
    local res = fn(...)
    print("after exec")
    return res
end
local f = function(p1, p2)
    -- fnId : 2  UpValues : lv6$log <- lv7$log 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  p1:lv3  p2:lv4  EnhancedFunc_1884e57536270d3db99:lv5  log:lv6  
    local EnhancedFunc_1884e57536270d3db99 = function(p1, p2)
        -- fnId : 3 
        -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  p1:lv3  p2:lv4  
        print("Do something")
    end
    return log(EnhancedFunc_1884e57536270d3db99, p1, p2)
end
