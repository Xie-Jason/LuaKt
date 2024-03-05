print "Hello"

@Type(number,number?) local v ,t = 1 + 2, 3

do
    @Type(java.io.File) local f = open("../../test.txt")
end


--[[ local f1 = function(fn, ...)
    print(n)
    local r = fn(...)
    print(n)
    return r
end ]]

local f2 = @Decorator(f1) function(n1,n2)
    return 2 * n1 * n2
end