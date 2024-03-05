--
t = {}
--

@Type(int,int) local v ,t = 1 + 2, 3

local num = 15 * 2 + 3 * 4 - 6 * 2 / 3

t[0] = 20

local function F()

end

@Sync do
    print(1 + 2 * 3 / 4)
end

fn = function()
    n = 1 + 1 * 2 / 2
    return n, "RE"
end