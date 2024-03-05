local flag = false
local value,num = 11,22

print(arg[0])

local hello = function(n)
    print(n * 5)
    return "Hello"
end

local function log(fn, ...)
    print("before exec")
    local res = fn(...)
    print("after exec")
    return res
end

local f = function(p1,p2)
    print("Do something")
end