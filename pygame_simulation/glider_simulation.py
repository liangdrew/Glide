import sys
import pygame
import math
import random
import time
from pygame.locals import *

pygame.init()

# screen variables
screen_width = 1200
screen_height = 600
screen_size = (screen_width, screen_height)

# colors
black = (0, 0, 0)
grey = (94, 92, 92)
white = (255, 255, 255)
red = (239, 33, 33)

# set the screen and surface
screen = pygame.display.set_mode(screen_size)
surface = pygame.Surface(screen_size).convert()
pygame.display.set_caption("Glider Physics Simulator")
surface.fill(white)
clock = pygame.time.Clock()

# position and size and vectors of the rod
center_x = screen_width/2
center_y = screen_height/3
length = screen_width/20
player_thickness = 1

# define points for the endpoints of the line segment representing the rod
start_x = 0
start_y = 0
end_x = 0
end_y = 0

# physics variables
angle = 0
angle_change = (math.pi)/30
gravity = 450
velocity = 0
dt = 0.03   # in seconds

# food variables initial values
x_pos = 0
speed = 0
radius = screen_width/100

# matrix for 1 food for testing purposes
food_matrix = [
    ["f1", x_pos, 0, speed]]

# food functions
def set_food_values():
    for food in food_matrix:
        food[1] = random.randint(20, screen_width - 20)  # set x_pos
        food[3] = random.randrange(1, 5, 1)

def draw_foods():
    for fo in food_matrix:
        fo[2] += fo[3]
        pygame.draw.circle(surface, red, (fo[1], fo[2]), radius, 0)

def check_status():
    for foo in food_matrix:
        if foo[2] >= screen_height:
            foo[2] = 0
            # food_matrix.remove(f)

# draw function
def draw_player():
    start_x = center_x - round(math.cos(angle) * length / 2)
    end_x = center_x + round(math.cos(angle) * length / 2)
    start_y = center_y + round(math.sin(angle) * length / 2)
    end_y = center_y - round(math.sin(angle) * length / 2)
    if start_x > end_x:
        a = start_x
        start_x = end_x
        end_x = a
        
        a = start_y
        start_y = end_y
        end_y = a
        
    pygame.draw.line(surface, black, (start_x, start_y), (end_x, end_y), player_thickness)
    return [start_x, start_y, end_x, end_y]

loop = True

set_food_values()

# main loop
while loop:
    clock.tick(dt*1000)
    surface.fill(white)

    # change the angle if the arrow keys are pressed
    keys = pygame.key.get_pressed()
    if keys[pygame.K_RIGHT]:
        angle -= angle_change
    elif keys[pygame.K_LEFT]:
        angle += angle_change

    # update acceleration, velocity, and position
    acceleration = gravity * (-math.sin(angle))
    velocity += acceleration * dt
    center_x += round(velocity*math.cos(angle) * dt)
    center_y -= round(velocity*math.sin(angle) * dt)

    # keep the angle between 0 and 2pi
    if angle > math.pi * 2:
        angle -= math.pi * 2
    elif angle < 0: 
        angle += math.pi * 2

    positions = draw_player()
    start_x = positions[0]
    start_y = positions[1]
    end_x = positions[2]
    end_y = positions[3]
    draw_foods()
    check_status()
    
    # if the player reaches the edge of the screen, move it to the opposite edge
    if center_x >= screen_width + length/2:
        center_x = -length/2

    elif center_x <= -length/2:
        center_x = screen_width + length/2

    if center_y >= screen_height + length/2:
        center_y = -length/2

    elif center_y <= -length/2:
        center_y = screen_height + length/2

    # check for collision
    for fo in food_matrix:
        t = (((fo[1] - start_x) * (end_x - start_x)) + ((fo[2] - start_y) * (end_y - end_x)))/(((end_x - start_x)**2) + ((end_y - start_y)**2))
        
        if t < 0:
            dist2 = (fo[1] - start_x)**2 + (fo[2] - start_y)**2
        elif 0 <= t <= 1:
            qx = start_x + t*(end_x - start_x)
            qy = start_y + t*(end_y - start_y)
            dist2 = (fo[1] - qx)**2 + (fo[2] - qy)**2
        elif t > 1:
            dist2 = (fo[1] - end_x)**2 + (fo[2] - end_y)**2

        if dist2 < radius**2:
            food_matrix.remove(fo)
            
            
            
    # check for quitting
    for ev in pygame.event.get():

        if ev.type == QUIT:
            loop = False
        if ev.type == KEYDOWN and ev.key == K_ESCAPE:
            loop = False
    

    screen.blit(surface, (0, 0))
    pygame.display.flip()

pygame.quit()
sys.exit()
