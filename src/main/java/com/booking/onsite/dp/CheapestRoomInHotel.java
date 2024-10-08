package com.booking.onsite.dp;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ooo
 * @description 酒店花费最小的房间组合
 * 给定一个酒店的房间信息列表，房间信息包含该房间的编号ID、价格和可以入住的人数上限，表示为 {id: 0, price: 180, guests: 1}；
 * 现在有一个旅行团需要在该酒店入住，请你使用Java实现一个算法，求出满足旅行团入住需求的最便宜价格。
 *
 *
 * follow up：求出满足旅行团入住需求的最便宜的房间列表。
 * @date 2024/9/25 10:24:52
 */
public class CheapestRoomInHotel {

    private static List<Room> bestRooms = new ArrayList<>();
    private static int minCost = Integer.MAX_VALUE;

    public static void main(String[] args) {
        CheapestRoomInHotel c = new CheapestRoomInHotel();
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(1, 180, 1));
        rooms.add(new Room(2, 200, 2));
        rooms.add(new Room(3, 300, 3));
        rooms.add(new Room(4, 160, 2));
        rooms.add(new Room(5, 150, 5));
        /*rooms.add(new Room(1, 180, 1)); // 1 guest
        rooms.add(new Room(2, 150, 2)); // 2 guests
        rooms.add(new Room(3, 200, 2)); // 2 guests
        rooms.add(new Room(4, 100, 1)); // 1 guest
        rooms.add(new Room(5, 120, 3)); // 3 guests*/

        int requiredGuests = 10;
        List<Room> roomList = c.findCheapestRooms3(rooms, requiredGuests);
        System.out.println("" + roomList.stream().mapToInt(p -> p.price).sum());
        System.out.println("" + roomList.stream().map(Objects::toString).collect(Collectors.joining(",")));
    }

    public static List<Room> findCheapestRooms3(List<Room> rooms, int totalGuests) {
        // Sort the rooms by price (ascending)
        Collections.sort(rooms, Comparator.comparingInt(room -> room.price));

        // Backtrack to find the best combination of rooms
        backtrack(rooms, totalGuests, new ArrayList<>(), 0, 0);
        return bestRooms;
    }

    private static void backtrack(List<Room> rooms, int remainingGuests, List<Room> currentRooms, int currentCost, int start) {
        // If we have met the guest requirement, check if we have a new minimum cost
        if (remainingGuests <= 0) {
            if (currentCost < minCost) {
                minCost = currentCost;
                bestRooms = new ArrayList<>(currentRooms);
            }
            return;
        }

        // Start from the current room index to avoid rechecking previous rooms
        for (int i = start; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            // If adding this room exceeds the current minimum cost, stop further exploration
            if (currentCost + room.price >= minCost) {
                continue;
            }

            currentRooms.add(room);
            backtrack(rooms, remainingGuests - room.guests, currentRooms, currentCost + room.price, i + 1);
            currentRooms.remove(currentRooms.size() - 1); // Backtrack
        }
    }

    public List<Room> findCheapestRoomsDP(List<Room> rooms, int totalGuests) {
        // 按价格升序排序房间
//        Collections.sort(rooms, Comparator.comparingInt(r -> r.price));

        int n = rooms.size();
        int[][] dp = new int[n + 1][totalGuests + 1];

        // 初始化 dp 数组
        for (int i = 0; i <= totalGuests; i++) {
            dp[0][i] = Integer.MAX_VALUE;
        }
        dp[0][0] = 0;

        // 动态规划填充 dp 数组
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j <= totalGuests; j++) {
                dp[i][j] = dp[i-1][j];
                if (j >= rooms.get(i-1).guests && dp[i-1][j - rooms.get(i-1).guests] != Integer.MAX_VALUE) {
                    dp[i][j] = Math.min(dp[i][j], dp[i-1][j - rooms.get(i-1).guests] + rooms.get(i-1).price);
                }
            }
        }

        // 如果无法容纳所有客人，返回空列表
        if (dp[n][totalGuests] == Integer.MAX_VALUE) {
            return new ArrayList<>();
        }

        // 回溯找出选择的房间
        List<Room> selectedRooms = new ArrayList<>();
        int i = n, j = totalGuests;
        while (i > 0 && j > 0) {
            if (dp[i][j] != dp[i-1][j]) {
                selectedRooms.add(rooms.get(i-1));
                j -= rooms.get(i-1).guests;
            }
            i--;
        }

        return selectedRooms;
    }
    public static List<Room> findCheapestRooms2(List<Room> rooms, int totalGuests) {
        int n = rooms.size();
        int[] dp = new int[totalGuests + 1];

        // 初始化dp数组
        for (int j = 1; j <= totalGuests; j++) {
            dp[j] = Integer.MAX_VALUE;
        }

        // 用于回溯的数组
        int[][] choice = new int[n + 1][totalGuests + 1];

        // 填充dp数组
        for (int i = 1; i <= n; i++) {
            Room room = rooms.get(i - 1);
            for (int j = totalGuests; j >= room.guests; j--) {
                choice[i][j] = 0;  // 0 表示不选择当前房间
                if (room.guests <= j && dp[j - room.guests] != Integer.MAX_VALUE) {
                    int newCost = dp[j - room.guests] + room.price;
                    if (newCost < dp[j]) {
                        dp[j] = newCost;
                        choice[i][j] = 1;  // 1 表示选择当前房间
                    }
                }
            }
        }

        // 如果无法安排所有客人
        if (dp[totalGuests] == Integer.MAX_VALUE) {
            return null;
        }

        // 回溯找出选择的房间
        List<Room> result = new ArrayList<>();
        int remainingGuests = totalGuests;
        for (int i = n; i > 0 && remainingGuests > 0; i--) {
            if (choice[i][remainingGuests] == 1) {
                Room room = rooms.get(i - 1);
                result.add(room);
                remainingGuests -= room.guests;
            }
        }

        return result;
    }
    public List<Room> findCheapestRooms(List<Room> rooms, int requiredGuests) {
        int[] dp = new int[requiredGuests + 1];
        Room[][] roomSelection = new Room[requiredGuests + 1][];

        int lowest = Integer.MAX_VALUE;
        Room cheapRoom = null;
        for (Room room : rooms) {
            if (room.guests >= requiredGuests && room.price < lowest){
                lowest = room.price;
                cheapRoom = room;
            }
        }
        if (cheapRoom != null){
            return Arrays.asList(cheapRoom);
        }

        // 初始化 dp 数组
        for (int i = 1; i <= requiredGuests; i++) {
            dp[i] = Integer.MAX_VALUE;
        }

        // 动态规划填充 dp 数组
        for (Room room : rooms) {
            for (int i = requiredGuests; i >= room.guests; i--) {
                if (dp[i - room.guests] != Integer.MAX_VALUE) {
                    int newCost = dp[i - room.guests] + room.price;
                    if (newCost < dp[i]) {
                        dp[i] = newCost;
                        roomSelection[i] = roomSelection[i - room.guests] == null
                                ? new Room[]{room}
                                : combineArrays(roomSelection[i - room.guests], room);
                    }
                }
            }
        }

        // 获取所有可能的房间组合
        List<Room> result = new ArrayList<>();
        if (dp[requiredGuests] == Integer.MAX_VALUE) {
            return result;  // 没有满足条件的组合
        }

        for (Room room : roomSelection[requiredGuests]) {
            result.add(room);
        }

        return result;
    }

    private Room[] combineArrays(Room[] array1, Room room) {
        Room[] result = new Room[array1.length + 1];
        System.arraycopy(array1, 0, result, 0, array1.length);
        result[array1.length] = room;
        return result;
    }
    /**
     * 典型的01背包问题，使用dp求解
     * dp[i] 表示 使 i 个人入住所需的最小花费
     * dp[0] = 0
     * 初始化：dp[i] = Integer.MAX_VALUE
     * transfer formula：dp[i] =
     * @param rooms
     * @param guestAmount
     * @return
     */
    public int cheapestPrice(List<Room> rooms, int guestAmount){
        /*int lowest = Integer.MAX_VALUE;
        for (Room room : rooms) {
            if (room.guests >= guestAmount && room.price < lowest){
                lowest = room.price;
            }
        }
        if (lowest != Integer.MAX_VALUE){
            return lowest;
        }*/

        int[] dp = new int[guestAmount + 1];
        for (int i = 1; i < dp.length; i++) {
            dp[i] = Integer.MAX_VALUE;
        }

        for (Room room : rooms) {
            // 对于每个room，检查能够住几个人，比如room最多住5个人而旅行团有12个人，那么dp[12]就可以由dp[7] + room.price得到；
            // 同理，dp[11]可以由dp[6] + room.price得到
            // 条件是 dp[7],dp[6]已经计算出来了
            for (int i = guestAmount; i >= room.guests; i--) {
                if(dp[i - room.guests] != Integer.MAX_VALUE){
                    dp[i] = Math.min(dp[i], dp[i - room.guests] + room.price);
                }
            }
        }

        return dp[guestAmount] == Integer.MAX_VALUE ? -1 : dp[guestAmount];
    }

    /**
     * follow up需要求解最小花费的房间列表，那么需要再增加一个二维数组存储已经选择的房间
     * @param rooms
     * @param guestAmount
     * @return
     */
    public List<Room> cheapestRooms(List<Room> rooms, int guestAmount){
        int[] dp = new int[guestAmount + 1];
        // [i, room]表示安排i个人入住时选择的Room id
        Room[] selectedRooms = new Room[guestAmount + 1];

        for (int i = 1; i <= guestAmount; i++) {
            dp[i] = Integer.MAX_VALUE;
        }

        for (Room room : rooms) {
            for (int i = guestAmount; i >= room.guests; i--){
                if (dp[i - room.guests] != Integer.MAX_VALUE){
                    int newCost = dp[i - room.guests] + room.price;
                    if (newCost < dp[i]){
                        dp[i] = newCost;
                        selectedRooms[i] = room;
                    }
                }
            }
        }
        if (dp[guestAmount] == Integer.MAX_VALUE){
            return null;
        }
        List<Room> ans = new ArrayList<>();
        int remaining = guestAmount;
        while (remaining > 0 && selectedRooms[remaining] != null){
            Room room = selectedRooms[remaining];
            if (ans.contains(room)){
                remaining--;
                continue;
            }
            ans.add(room);
            selectedRooms[remaining] = null;
            remaining -= room.guests;
        }

        return ans;
    }

    static class Room{
        int id;
        int price;
        int guests;

        public Room(int id, int price, int guests) {
            this.id = id;
            this.price = price;
            this.guests = guests;
        }

        @Override
        public String toString() {
            return "Room{" +
                    "id=" + id +
                    ", price=" + price +
                    ", guests=" + guests +
                    '}';
        }
    }
}
