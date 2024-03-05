
local count = 0
for idx = 0,10 do
    count = count * 2
end

for idx = 0,10,2 do
    count = count / idx
end

for k,v in pairs({}) do
    print(k,v)
end

while count - 1 > 0 do
    count = count - 1
end

repeat
    count = count + 3
until count < 100

print(count)