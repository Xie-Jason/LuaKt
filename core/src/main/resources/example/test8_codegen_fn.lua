
local n, i, j, k  = 10,20,30,40,50
print("-----")
print(n,i,j,k)

n = 200

print(n)

local func = function()
    print("Hi!!!")
    return 20,30
end

func()
print(func())

local f = function(n1,n2,n3)
    print(n1,n2,n3)
end

f(func(),2,3)

f(2)