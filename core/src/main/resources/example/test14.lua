local n0 = 0
function wrap()
    local n1,n2 = 1,2,3
    local function hello()
        print("Hello",n0,n1,n2)
    end
    hello()
end

print("参数1","参数2","参数3")

print("22,11,33,444":split(","))

wrap()

function loop()
    local i = 10
    while i < 10 do
        i = i + 1
    end
end